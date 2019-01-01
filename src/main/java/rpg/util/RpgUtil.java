package rpg.util;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import io.netty.channel.Channel;
import rpg.pojo.Level;
import rpg.pojo.Monster;
import rpg.pojo.User;
import rpg.pojo.Userbag;
import rpg.pojo.Yaopin;
import rpg.pojo.Zb;
import rpg.session.IOsession;
import rpg.task.TaskManage;

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
	 * 装备入包带耐久度
	 * 
	 * @param sendUser
	 * @param zb
	 * @param njd
	 */
	public static void putZbWithNJD(User sendUser, Zb zb, Integer njd) {
		List<Userbag> list = IOsession.userBagMp.get(sendUser);
		Userbag userbag = new Userbag();
		userbag.setId(UUID.randomUUID().toString());
		userbag.setUsername(sendUser.getNickname());
		userbag.setGid(zb.getId());
		userbag.setNumber(1);
		userbag.setNjd(njd);
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
	 * 药品入包带数量
	 * 
	 * @param user
	 * @param zb
	 */
	public static void putYaopin(User user, Yaopin yaopin, int num) {
		List<Userbag> list = IOsession.userBagMp.get(user);
		boolean flag = false;
		for (Userbag userbag1 : list) {
			if (yaopin.getId() == userbag1.getGid()) {
				userbag1.setNumber(userbag1.getNumber() + num);
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
	 * 
	 * @param user
	 * @param ch
	 */
	public static void ackEnd(User user, Channel ch, Monster monster) {
		StringBuilder string = new StringBuilder();
		int exp = monster.getExp();
		int checkLevel = checkLevel(user, exp, string);
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
			string.append("获得金钱：" + monster.getMoney() + "获得装备：" + zb.getName() + "\n");
		} else if (yaopin != null) {
			putYaopin(user, yaopin);
			string.append("获得金钱：" + monster.getMoney() + "获得药品：" + yaopin.getName() + "\n");
		}
		String s = "" + string;
		SendMsg.send(s, ch);
		TaskManage.checkMoneyTaskCompleteBytaskid(user, 11);
		if (checkLevel == 1)
			TaskManage.checkTaskCompleteBytaskid(user, 2);
	}

	/**
	 * 检查等级
	 * 
	 * @param user
	 * @param exp
	 * @param string
	 * @return
	 */
	private static int checkLevel(User user, int exp, StringBuilder string) {
		int userlevel = user.getLevel();
		int userexp = user.getExp();
		Level level = IOsession.levelMp.get(userlevel + 1);
		if (userexp + exp >= level.getExpl()) {
			userexp = userexp + exp - level.getExpl();
			user.setExp(userexp);
			user.setLevel(userlevel + 1);
			string.append("恭喜你升级---当前等级" + user.getLevel() + "---当前经验" + user.getExp() + "/" + level.getExpr() + "\n");
			return 1;
		} else {
			user.setExp(userexp + exp);
			string.append("获得经验" + exp + "\n");
			return 0;
		}
	}
}
