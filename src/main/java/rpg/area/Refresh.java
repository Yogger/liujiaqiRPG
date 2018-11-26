package rpg.area;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import rpg.pojo.Buff;
import rpg.pojo.User;
import rpg.session.IOsession;

public class Refresh implements Runnable {

	@Override
	public synchronized void run() {
//		System.out.println("开始执行了：");
		if (IOsession.mp != null) {
			for (User user : IOsession.mp.values()) {
				HashMap<Integer, Long> buffTime = IOsession.buffTimeMp.get(user);
				int addMp = 5;
				if (buffTime != null) {
					for (Entry<Integer, Long> entry : buffTime.entrySet()) {
//				System.out.println("id"+entry.getKey()+"时间"+entry.getValue());
						// 通过buffID找到具体的buff
						Integer buffId = entry.getKey();
						Buff buff = IOsession.buffMp.get(buffId);
						// 获取使用Buff的时间
						Long lastTime = entry.getValue();
						long currentTimeMillis = System.currentTimeMillis();
						if (currentTimeMillis - lastTime < buff.getLastedTime()) {
							addMp += buff.getMp();
						}
					}
				}
				if (user.getMp() < 100) {
					if (user.getMp() + addMp > 100) {
						user.getAndSetMp(user, 100);
					} else {
						user.getAndSetMp(user, user.getMp() + addMp);
					}
					System.out.println(user.getNickname() + "-当前蓝量：" + user.getMp());
				}
			}
		}
	}
}
