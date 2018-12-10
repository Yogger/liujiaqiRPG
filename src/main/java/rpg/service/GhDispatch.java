package rpg.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.util.internal.StringUtil;
import rpg.data.dao.GhMapper;
import rpg.data.dao.GhstoreMapper;
import rpg.data.dao.GhuserMapper;
import rpg.data.dao.UserMapper;
import rpg.pojo.Gh;
import rpg.pojo.GhExample;
import rpg.pojo.Ghuser;
import rpg.pojo.GhuserExample;
import rpg.pojo.GhuserExample.Criteria;
import rpg.pojo.User;
import rpg.session.IOsession;

/**
 * 工会逻辑
 * 
 * @author ljq
 *
 */
@Component
public class GhDispatch {

	@Autowired
	private GhMapper ghMapper;
	@Autowired
	private GhuserMapper ghuserMapper;
	@Autowired
	private GhstoreMapper ghstoreMapper;
	@Autowired
	private UserMapper userMapper;

	public void gh(User user, Channel ch, ChannelGroup group, String msgR) {
		String[] msg = msgR.split("\\s+");
		if (msg.length == 3 && msg[1].equals("creat")) {
			creatGh(user, ch, msg);
		} else if (msg.length == 2 && msg[1].equals("show")) {
			showGh(user, ch, group, msg);
		} else if (msg.length == 3 && msg[1].equals("join")) {
			joinGh(user, ch, group, msg);
		} else if (msg.length == 3 && msg[1].equals("accept")) {
			acceptGh(user, ch, group, msg);
		} else if (msg.length == 2 && msg[1].equals("showsq")) {
			showsqGh(user, ch, group, msg);
		} else if (msg.length == 2 && msg[1].equals("showuser")) {
			showuserGh(user, ch, group, msg);
		} else if (msg.length == 3 && msg[1].equals("t")) {
			tGh(user, ch, group, msg);
		} else if (msg.length == 2 && msg[1].equals("js")) {
			jsGh(user, ch, group, msg);
		} else if (msg.length == 2 && msg[1].equals("esc")) {
			quitGh(user, ch, group, msg);
		} else if (msg.length == 4 && msg[1].equals("raise")) {
			raiseGh(user, ch, group, msg);
		} else if(msg.length==3&&msg[1].equals("down")) {
			downGh(user, ch, group, msg);
		}
	}

	private void downGh(User user, Channel ch, ChannelGroup group, String[] msg) {
		GhuserExample example = new GhuserExample();
		Criteria criteria = example.createCriteria();
		criteria.andUsernameEqualTo(msg[2]);
		GhuserExample example2 = new GhuserExample();
		Criteria criteria2 = example2.createCriteria();
		criteria2.andUsernameEqualTo(user.getNickname());
		List<Ghuser> list2 = ghuserMapper.selectByExample(example2);
		if (list2 != null && list2.size() > 0) {
			Ghuser ghuser = list2.get(0);
			List<Ghuser> list = ghuserMapper.selectByExample(example);
			if (list != null && list.size() > 0) {
				Ghuser ghuser2 = list.get(0);
				if (ghuser.getPower() < ghuser2.getPower()) {
					if (msg[3].equals("3")&&ghuser2.getPower()<3) {
						if (ghuser.getPower() < 2) {
							ghuser2.setPower(3);
							ghuser2.setJobname("精英");
							ghuserMapper.updateByExample(ghuser2, example);
							User user2 = IOsession.nameMap.get(msg[2]);
							Channel channel = IOsession.userchMp.get(user2);
							ch.writeAndFlush("降低职位成功");
							channel.writeAndFlush("你被降级为精英");
						} else {
							ch.writeAndFlush("权限不够,不能降级该职位");
						}
					} else if (msg[3].equals("4")&&ghuser2.getPower()<4) {
						if (ghuser.getPower() < 3) {
							ghuser2.setPower(4);
							ghuser2.setJobname("成员");
							ghuserMapper.updateByExample(ghuser2, example);
							User user2 = IOsession.nameMap.get(msg[2]);
							Channel channel = IOsession.userchMp.get(user2);
							ch.writeAndFlush("降低职位成功");
							channel.writeAndFlush("你被降级为成员");
						} else {
							ch.writeAndFlush("权限不够,不能降级该职位");
						}
					} else {
						ch.writeAndFlush("指令错误");
					}
				} else {
					ch.writeAndFlush("权限不够,不能降级该玩家头衔");
				}
			} else {
				ch.writeAndFlush("不存在该玩家");
			}
		} else {
			ch.writeAndFlush("你不在工会");
		}
	}

	private void raiseGh(User user, Channel ch, ChannelGroup group, String[] msg) {
		GhuserExample example = new GhuserExample();
		Criteria criteria = example.createCriteria();
		criteria.andUsernameEqualTo(msg[2]);
		GhuserExample example2 = new GhuserExample();
		Criteria criteria2 = example2.createCriteria();
		criteria2.andUsernameEqualTo(user.getNickname());
		List<Ghuser> list2 = ghuserMapper.selectByExample(example2);
		if (list2 != null && list2.size() > 0) {
			Ghuser ghuser = list2.get(0);
			List<Ghuser> list = ghuserMapper.selectByExample(example);
			if (list != null && list.size() > 0) {
				Ghuser ghuser2 = list.get(0);
				if (ghuser.getPower() < ghuser2.getPower() - 1) {
					if (msg[3].equals("3")) {
						if (ghuser.getPower() < 3) {
							ghuser2.setPower(3);
							ghuser2.setJobname("精英");
							ghuserMapper.updateByExample(ghuser2, example);
							User user2 = IOsession.nameMap.get(msg[2]);
							Channel channel = IOsession.userchMp.get(user2);
							ch.writeAndFlush("提升职位成功");
							channel.writeAndFlush("你被提升为精英");
						} else {
							ch.writeAndFlush("权限不够,不能提升该职位");
						}
					} else if (msg[3].equals("2")) {
						if (ghuser.getPower() < 2) {
							ghuser2.setPower(2);
							ghuser2.setJobname("副会长");
							ghuserMapper.updateByExample(ghuser2, example);
							User user2 = IOsession.nameMap.get(msg[2]);
							Channel channel = IOsession.userchMp.get(user2);
							ch.writeAndFlush("提升职位成功");
							channel.writeAndFlush("你被提升为副会长");
						} else {
							ch.writeAndFlush("权限不够,不能提升该职位");
						}
					} else {
						ch.writeAndFlush("指令错误");
					}
				} else {
					ch.writeAndFlush("权限不够,不能提升该玩家头衔");
				}
			} else {
				ch.writeAndFlush("不存在该玩家");
			}
		} else {
			ch.writeAndFlush("你不在工会");
		}
	}

	private void quitGh(User user, Channel ch, ChannelGroup group, String[] msg) {
		GhuserExample example = new GhuserExample();
		Criteria criteria = example.createCriteria();
		criteria.andUsernameEqualTo(user.getNickname());
		ghuserMapper.deleteByExample(example);
		user.setGhId(0);
		userMapper.updateByPrimaryKey(user);
		ch.writeAndFlush("退出工会成功");
	}

	private void jsGh(User user, Channel ch, ChannelGroup group, String[] msg) {
		
	}

	private void tGh(User user, Channel ch, ChannelGroup group, String[] msg) {
		GhuserExample example = new GhuserExample();
		Criteria criteria = example.createCriteria();
		criteria.andUsernameEqualTo(msg[2]);
		GhuserExample example2 = new GhuserExample();
		Criteria criteria2 = example2.createCriteria();
		criteria2.andUsernameEqualTo(user.getNickname());
		List<Ghuser> list2 = ghuserMapper.selectByExample(example2);
		if (list2 != null && list2.size() > 0) {
			Ghuser ghuser = list2.get(0);
			List<Ghuser> list = ghuserMapper.selectByExample(example);
			if (list != null && list.size() > 0) {
				Ghuser ghuser2 = list.get(0);
				if (ghuser.getPower() < ghuser2.getPower()) {
					ghuserMapper.deleteByExample(example);
					User user2 = IOsession.nameMap.get(msg[2]);
					user2.setGhId(0);
					userMapper.updateByPrimaryKey(user2);
					Channel channel = IOsession.userchMp.get(user2);
					ch.writeAndFlush("已将该玩家踢出");
					channel.writeAndFlush("你被踢出工会");
				} else {
					ch.writeAndFlush("权限不够,不能移除该玩家");
				}
			} else {
				ch.writeAndFlush("不存在该玩家");
				return;
			}
		} else {
			ch.writeAndFlush("你不在工会");
			return;
		}
	}

	private void showuserGh(User user, Channel ch, ChannelGroup group, String[] msg) {
		GhuserExample example = new GhuserExample();
		List<Ghuser> list = ghuserMapper.selectByExample(example);
		String string = "";
		for (Ghuser ghuser : list) {
			string += "用户名:" + ghuser.getUsername() + "---职位:" + ghuser.getJobname() + "\n";
		}
		ch.writeAndFlush(string);
	}

	private void showsqGh(User user, Channel ch, ChannelGroup group, String[] msg) {
		if (user.getGhId() != 0) {
			String string = "";
			HashMap<String, Long> map = IOsession.ghsqMp.get(user.getGhId());
			if (map != null) {
				for (Entry<String, Long> entry : map.entrySet()) {
					string += "申请人：" + entry.getKey() + "---申请时间:" + entry.getValue();
				}
				ch.writeAndFlush(string);
			}
		} else {
			ch.writeAndFlush("你没有工会");
		}
	}

	private void acceptGh(User user, Channel ch, ChannelGroup group, String[] msg) {
		int ghId = user.getGhId();
		if (ghId != 0) {
			HashMap<String, Long> map = IOsession.ghsqMp.get(user.getGhId());
			if (map.containsKey(msg[2])) {
				User user2 = IOsession.nameMap.get(msg[2]);
				String username = user2.getNickname();
				user2.setGhId(ghId);
				userMapper.updateByPrimaryKey(user2);
				Ghuser ghuser = new Ghuser();
				ghuser.setId(ghId);
				ghuser.setUsername(username);
				ghuser.setPower(4);
				ghuser.setJobname("成员");
				ghuserMapper.insert(ghuser);
				ch.writeAndFlush(user2.getNickname() + "加入工会");
				Channel channel = IOsession.userchMp.get(user2);
				channel.writeAndFlush("你成功加入工会");
			} else {
				ch.writeAndFlush("指令错误");
			}
		} else {
			ch.writeAndFlush("你没有工会");
		}
	}

	private void joinGh(User user, Channel ch, ChannelGroup group, String[] msg) {
//		HashMap<String, Ghuser> map = IOsession.ghUserMp.get(Integer.valueOf(msg[2]));
//		for (Ghuser ghuser : map.values()) {
//			if (ghuser.getPower() < 4) {
//				User user2 = IOsession.nameMap.get(ghuser.getUsername());
//				Channel channel = IOsession.userchMp.get(user2);
//				ch.writeAndFlush("申请已发送");
//				channel.writeAndFlush(user.getNickname()+"---申请加入工会");
//			}
//		}
		GhuserExample example = new GhuserExample();
		Criteria criteria = example.createCriteria();
		if (StringUtils.isNumeric(msg[2])) {
			criteria.andIdEqualTo(Integer.valueOf(msg[2]));
			List<Ghuser> list = ghuserMapper.selectByExample(example);
			for (Ghuser ghuser : list) {
				if (ghuser.getPower() < 4) {
					User user2 = IOsession.nameMap.get(ghuser.getUsername());
					Channel channel = IOsession.userchMp.get(user2);
					HashMap<String, Long> hashMap = new HashMap<>();
					String name = user.getNickname();
					Integer id = ghuser.getId();
					hashMap.put(name, System.currentTimeMillis());
					IOsession.ghsqMp.put(id, hashMap);
					ch.writeAndFlush("申请已发送");
					channel.writeAndFlush(user.getNickname() + "---申请加入工会");
				}
			}
		} else {
			ch.writeAndFlush("指令错误");
		}
	}

	private void creatGh(User user, Channel ch, String[] msg) {
		String name = user.getNickname();
		Gh gh = new Gh();
		gh.setName(msg[2]);
		gh.setCreatname(name);
		gh.setLevel(1);
		ghMapper.insertSelective(gh);
		Integer id = gh.getId();
		Ghuser ghuser = new Ghuser();
		ghuser.setId(id);
		ghuser.setUsername(name);
		ghuser.setPower(1);
		ghuser.setJobname("会长");
		ghuserMapper.insertSelective(ghuser);
		user.setGhId(id);
		userMapper.updateByPrimaryKey(user);
		// 存入缓存
//		IOsession.ghMp.put(id, gh);
//		HashMap<String, Ghuser> map = new HashMap<>();
//		map.put(name, ghuser);
//		IOsession.ghUserMp.put(id, map);
		ch.writeAndFlush("创建工会成功");
	}

	private void showGh(User user, Channel ch, ChannelGroup group, String[] msg) {
		GhExample ghExample = new GhExample();
		List<Gh> list = ghMapper.selectByExample(ghExample);
		String req = "";
		for (Gh gh : list) {
			req += gh.getId() + "---公会名：" + gh.getName() + "---创建者名：" + gh.getCreatname() + "---工会等级：" + gh.getLevel()
					+ "\n";
		}
//		if (IOsession.ghMp != null) {
//			for (Gh gh : IOsession.ghMp.values()) {
//				req += gh.getId() + "---公会名：" + gh.getName() + "---创建者名：" + gh.getCreatname() + "---工会等级："
//						+ gh.getLevel() + "\n";
//			}
//		}
		ch.writeAndFlush(req);
	}
}
