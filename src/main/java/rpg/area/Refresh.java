package rpg.area;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import rpg.pojo.Buff;
import rpg.pojo.Jy;
import rpg.pojo.User;
import rpg.session.IOsession;

public class Refresh implements Runnable {

	@Override
	public synchronized void run() {
//		System.out.println("开始执行了：");
		try {
		if (IOsession.mp != null) {
			for (User user : IOsession.mp.values()) {
//				if(user.getJySendFlag()==1) {
//					Jy jy = IOsession.jyMap.get(user.getJyId());
//					if(jy!=null) {
//						if(System.currentTimeMillis()-jy.getStartTime()>10000) {
//							user.getAndSetjySendFlag(user, 0);
//							IOsession.jyMap.remove(user.getJyId());
//						}
//					}
//				}
				if(user.getLiveFlag()!=1) {
				ConcurrentHashMap<Integer, Long> buffTime = IOsession.buffTimeMp.get(user);
				int addMp = 5;
				int subHp = 0;
				if (buffTime != null) {
					for (Entry<Integer, Long> entry : buffTime.entrySet()) {
//				System.out.println("id"+entry.getKey()+"时间"+entry.getValue());
						// 通过buffID找到具体的buff
						Integer buffId = entry.getKey();
						if(buffId!=null) {
						Buff buff = IOsession.buffMp.get(buffId);
						// 获取使用Buff的时间
						Long lastTime = entry.getValue();
						if(lastTime!=null) {
						long currentTimeMillis = System.currentTimeMillis();
						if(buff!=null) {
						if (currentTimeMillis - lastTime < buff.getLastedTime()) {
							switch (buffId) {
							case 1:
								addMp += buff.getMp();
								break;
							case 2:
								subHp+=buff.getMp();
								user.getAndSetHp(user, user.getHp()-subHp);
								System.out.println(user.getNickname() +"-血量减少"+subHp+"-当前血量:"+user.getHp());
								break;
							default:
								break;
							}
//							addMp += buff.getMp();
						} else {
							switch (buffId) {
							case 3:
								Long long1 = buffTime.get(3);long1=null;
								buffTime.remove(3);
								break;
							case 4:
								Long long2 = buffTime.get(4);long2=null;
								buffTime.remove(4);
								break;
							default:
								break;
							}
						}
					
						}
					}
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
	}catch (Exception e) {
		e.printStackTrace();
	}
	}
}
