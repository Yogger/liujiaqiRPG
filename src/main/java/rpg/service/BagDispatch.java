package rpg.service;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import rpg.data.dao.UserbagMapper;
import rpg.data.dao.UserzbMapper;
import rpg.pojo.User;
import rpg.pojo.UserAttribute;
import rpg.pojo.Userbag;
import rpg.pojo.Userzb;
import rpg.pojo.Yaopin;
import rpg.pojo.Zb;
import rpg.session.IOsession;

@Component
public class BagDispatch {

	@Autowired
	private UserbagMapper userbagMapper;
	@Autowired
	private UserzbMapper userzbMapper;

	// 展示背包
	public void showBag(User user, Channel ch, ChannelGroup group) {
//		String nickname = user.getNickname();
//		UserbagExample example = new UserbagExample();
//		Criteria criteria = example.createCriteria();
//		criteria.andUsernameEqualTo(nickname);
//		List<Userbag> list = userbagMapper.selectByExample(example);
		List<Userbag> list = IOsession.userBagMp.get(user);
		String yaopinWord = "";
		String zbWord = "";
		for (Userbag userbag : list) {
			Yaopin yaopin = IOsession.yaopinMp.get(userbag.getGid());
			Zb zb = IOsession.zbMp.get(userbag.getGid());
			if (yaopin != null)
				yaopinWord += "格子id:" + userbag.getId() + "---" + yaopin.getName() + "---" + userbag.getNumber() + "\n";
			else {
				zbWord += "格子id:" + userbag.getId() + "---" + zb.getName() + "-耐久度：" + userbag.getNjd() + "-攻击力"
						+ zb.getAck() + "\n";
			}
		}
		UserAttribute attribute = IOsession.attMp.get(user);
		ch.writeAndFlush("用户金币：" + user.getMoney() + "\n" + "用户血量：" + user.getHp() + "\n" + "用户攻击力："
				+ attribute.getAck() + "\n" + yaopinWord + zbWord);
	}

	// 展示装备
	public void showZb(User user, Channel ch, ChannelGroup group) {
//		String nickname = user.getNickname();
//		UserzbExample example = new UserzbExample();
//		rpg.pojo.UserzbExample.Criteria criteria = example.createCriteria();
//		criteria.andUsernameEqualTo(nickname);
//		List<Userzb> list = userzbMapper.selectByExample(example);
		List<Userzb> list = IOsession.userZbMp.get(user);
		String msg = "";
		for (Userzb userzb : list) {
			Zb zb = IOsession.zbMp.get(userzb.getZbid());
			int njd = userzb.getNjd() > 0 ? userzb.getNjd() : 0;
			msg += zb.getName() + "-耐久度：" + njd + "-攻击力" + zb.getAck() + "\n";
		}
		ch.writeAndFlush(msg);
	}

	// 脱装备
	public void tkffZb(User user, Channel ch, ChannelGroup group, String msgR) {
		String[] msg = msgR.split("\\s+");
		String nickname = user.getNickname();
//		UserzbExample example1 = new UserzbExample();
//		rpg.pojo.UserzbExample.Criteria criteria = example1.createCriteria();
//		criteria.andUsernameEqualTo(nickname);
//		List<Userzb> list = userzbMapper.selectByExample(example1);
		List<Userzb> list = IOsession.userZbMp.get(user);
//		UserbagExample example3 = new UserbagExample();
//		Criteria criteria1 = example3.createCriteria();
//		criteria1.andUsernameEqualTo(nickname);
//		List<Userbag> list2 = userbagMapper.selectByExample(example3);
		List<Userbag> list2 = IOsession.userBagMp.get(user);
		if (msg.length == 2) {
			for (Userzb userzb : list) {
				Zb zb = IOsession.zbMp.get(userzb.getZbid());
				if (zb != null) {
					if (zb.getName().equals(msg[1])) {
						// 脱下装备
//				UserzbExample example2 = new UserzbExample();
//				rpg.pojo.UserzbExample.Criteria createCriteria = example2.createCriteria();
//				createCriteria.andZbidEqualTo(userzb.getZbid());
						UserAttribute attribute = IOsession.attMp.get(user);
						attribute.setAck(attribute.getAck() - zb.getAck() * userzb.getIsuse());
						list.remove(userzb);
//				userzbMapper.deleteByExample(example2);
						ch.writeAndFlush(
								"脱下装备成功" + "-攻击下降：" + zb.getAck() * userzb.getIsuse() + "现在攻击力" + attribute.getAck());
						// 放入背包
						Userbag userbag = new Userbag();
						String userbagId = UUID.randomUUID().toString();
						userbag.setId(userbagId);
						userbag.setUsername(nickname);
						userbag.setGid(zb.getId());
						userbag.setNumber(1);
						userbag.setNjd(userzb.getNjd());
						userbag.setIsadd(0);
//				userbagMapper.insertSelective(userbag);
						list2.add(userbag);
						break;
					}
				}
			}
		} else {
			ch.writeAndFlush("没有此物品");
		}
	}

	// 穿装备
	public void wearzb(User user, Channel ch, ChannelGroup group, String msgR) {
		String[] msg = msgR.split("\\s+");
		String nickname = user.getNickname();
//		UserbagExample example = new UserbagExample();
//		Criteria criteria = example.createCriteria();
//		criteria.andUsernameEqualTo(nickname);
//		List<Userbag> list = userbagMapper.selectByExample(example);
		List<Userbag> list = IOsession.userBagMp.get(user);
		List<Userzb> list2 = IOsession.userZbMp.get(user);
		if (msg.length == 2) {
			for (Userbag userbag : list) {
				if (userbag.getId().equals(msg[1])) {
					if (list2.size() != 0) {
						ch.writeAndFlush("请先脱下装备");
						break;
					} else {
						// 从背包移除装备
//					UserbagExample example2 = new UserbagExample();
//					Criteria createCriteria = example2.createCriteria();
//					createCriteria.andGidEqualTo(userbag.getGid());
//					userbagMapper.deleteByExample(example2);
						Zb zb = IOsession.zbMp.get(userbag.getGid());
						list.remove(userbag);
						// 放入装备栏
						Userzb userzb = new Userzb();
						userzb.setUsername(nickname);
						userzb.setZbid(userbag.getGid());
						userzb.setNjd(userbag.getNjd());
						if (userbag.getNjd() <= 0) {
							userzb.setIsuse(0);
						} else
							userzb.setIsuse(1);
						UserAttribute attribute = IOsession.attMp.get(user);
						attribute.setAck(attribute.getAck() + zb.getAck() * userzb.getIsuse());
						list2.add(userzb);
//					userzbMapper.insert(userzb);
						ch.writeAndFlush(
								"穿戴装备成功" + "-攻击上升：" + zb.getAck() * userzb.getIsuse() + "现在攻击力" + attribute.getAck());
						break;
					}
				}
			}
		} else {
			ch.writeAndFlush("没有此物品");
		}
	}

	// 修理装备
	public void fix(User user, Channel ch, ChannelGroup group, String msgR) {
		String[] msg = msgR.split("\\s+");
		String nickname = user.getNickname();
		List<Userzb> list = IOsession.userZbMp.get(user);
		if (msg.length == 2) {
			for (Userzb userzb : list) {
				Zb zb = IOsession.zbMp.get(userzb.getZbid());
				if (zb != null) {
					if (zb.getName().equals(msg[1])) {
						userzb.setNjd(10);
						UserAttribute attribute = IOsession.attMp.get(user);
						attribute.setAck(attribute.getAck() + zb.getAck());
						userzb.setIsuse(1);
						ch.writeAndFlush("装备修理成功");
					}
				}
			}
		} else {
			ch.writeAndFlush("没有此物品");
		}
	}
}