package rpg.util;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import io.netty.channel.Channel;
import rpg.pojo.Monster;
import rpg.pojo.User;
import rpg.pojo.Userbag;
import rpg.pojo.Yaopin;
import rpg.pojo.Zb;
import rpg.session.IOsession;

/**
 * 工具类
 * 
 * @author ljq
 *
 */
public class RpgUtil {

	/**
	 * 装备入包
	 * 
	 * @param user
	 */
	public static void putZb(User user, Zb zb) {
		List<Userbag> list = IOsession.userBagMp.get(user);
		Userbag userbag = new Userbag();
		userbag.setId(UUID.randomUUID().toString());
		userbag.setUsername(user.getNickname());
		userbag.setGid(zb.getId());
		userbag.setNumber(1);
		userbag.setNjd(zb.getNjd());
		userbag.setIsadd(0);
		list.add(userbag);
	}

	/**
	 * 药品入包
	 * 
	 * @param user
	 * @param zb
	 */
	public static void putYaopin(User user, Yaopin yaopin) {
		List<Userbag> list = IOsession.userBagMp.get(user);
		boolean flag = false;
		for (Userbag userbag1 : list) {
			if (yaopin.getId() == userbag1.getGid()) {
				userbag1.setNumber(userbag1.getNumber() + 1);
				flag = true;
				break;
			}
		}
		if (!flag) {
			Userbag userbag = new Userbag();
			userbag.setId(UUID.randomUUID().toString());
			userbag.setUsername(user.getNickname());
			userbag.setGid(yaopin.getId());
			userbag.setNumber(1);
			userbag.setNjd(0);
			userbag.setIsadd(1);
			list.add(userbag);
		}
	}

	/**
	 * 战斗结算奖励
	 * @param user
	 * @param ch
	 */
	public static void ackEnd(User user, Channel ch, Monster monster) {
		List<Integer> awardList = monster.getAwardList();
		Random random = new Random();
		int randomId = random.nextInt(awardList.size());
		user.setMoney(user.getMoney() + monster.getMoney());
		// 奖励物品id
		Integer id = awardList.get(randomId);
		Zb zb = IOsession.zbMp.get(id);
		Yaopin yaopin = IOsession.yaopinMp.get(id);
		if (zb != null) {
			putZb(user, zb);
			ch.writeAndFlush("获得金钱：" + monster.getMoney() + "获得装备：" + zb.getName());
		} else if (yaopin != null) {
			putYaopin(user, yaopin);
			ch.writeAndFlush("获得金钱：" + monster.getMoney() + "获得药品：" + yaopin.getName());
		}
	}
}
