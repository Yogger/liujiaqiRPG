package rpg.service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import rpg.pojo.Jy;
import rpg.pojo.User;
import rpg.pojo.Userbag;
import rpg.pojo.Yaopin;
import rpg.pojo.Zb;
import rpg.session.IOsession;
import rpg.task.TaskManage;
import rpg.util.RpgUtil;
import rpg.util.SendMsg;

/**
 * 交易逻辑
 * 
 * @author ljq
 *
 */
@Component
public class JyDispatch {

	private Lock lock = new ReentrantLock();
	private Lock lock1 = new ReentrantLock();

	public void jy(User user, Channel ch, ChannelGroup group, String msgR) {
		String[] msg = msgR.split("\\s+");
		// 接受jy请求 指令：jy yes 交易号
		if (msg[1].equals("yes") && msg.length > 1) {
			acceptJy(user, ch, group, msg);
		}
		// 拒绝jy请求 指令：jy no 交易号
		else if (msg[1].equals("no") && msg.length > 1) {
		}
		// 发送jy请求 指令：jy 用户
		else if (msg.length == 2 && !msg[1].equals("esc")) {
			sendJy(user, ch, group, msg);
		}
		// 取消交易请求
		else if (msg.length == 2 && msg[1].equals("esc")) {
			lock.lock();
			try {
				user.getAndSetjySendFlag(user, 0);
				Jy jy = IOsession.jyMap.get(user.getJyId());
				jy = null;
				IOsession.jyMap.remove(user.getJyId());
				SendMsg.send("交易已取消",ch);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				lock.unlock();
			}
		} else {
			SendMsg.send("指令错误",ch);
		}
	}

	public void jyProcess(User user, Channel ch, ChannelGroup group, String msgR) {
		String[] msg = msgR.split("\\s+");
		if (msg.length == 1 && msg[0].equals("esc")) {
			lock1.lock();
			try {
				user.getAndSetjyFlag(user, 0);
				Jy jy = IOsession.jyMap.get(user.getJyId());
				User sendUser = jy.getSendUser();
				if (sendUser.equals(user)) {
					sendUser = jy.getAcceptUser();
				}
				sendUser.getAndSetjyFlag(sendUser, 0);
				jy = null;
				IOsession.jyMap.remove(user.getJyId());
				Channel channel = IOsession.userchMp.get(sendUser);
				SendMsg.send("对方已取消交易",channel);
				SendMsg.send("交易取消",ch);
				return;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				lock1.unlock();
			}
		} else if (msg.length == 1 && msg[0].equals("y") && user.getJyFlag() == 2) {
			lock1.lock();
			try {
				Jy jy = IOsession.jyMap.get(user.getJyId());
				int acceptFlag = jy.getAcceptFlag();
				if (acceptFlag == 0) {
					jy.setAcceptFlag(1);
					User sendUser = jy.getSendUser();
					if (sendUser.equals(user)) {
						sendUser = jy.getAcceptUser();
					}
					SendMsg.send("你已确认了",ch);
					Channel channel = IOsession.userchMp.get(sendUser);
					SendMsg.send("对方已确认",channel);
				} else {
					User acceptUser = jy.getAcceptUser();
					User sendUser = jy.getSendUser();
					exchange(jy, acceptUser, sendUser);
					exchange(jy, sendUser, acceptUser);
					acceptUser.getAndSetjyFlag(acceptUser, 0);
					sendUser.getAndSetjyFlag(sendUser, 0);
					jy=null;
					IOsession.jyMap.remove(user.getJyId());
					Channel channel = IOsession.userchMp.get(sendUser);
					SendMsg.send("交易成功",channel);
					Channel channel2 = IOsession.userchMp.get(acceptUser);
					SendMsg.send("交易成功",channel2);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				lock1.unlock();
			}
		}

		else if (msg.length == 2 && user.getJyFlag() == 1) {
			if (!msg[0].equals("0")) {
				List<Userbag> list = IOsession.userBagMp.get(user);
				boolean flag = false;
				for (Userbag userbag : list) {
					if (userbag.getId().equals(msg[0])) {
						Jy jy = IOsession.jyMap.get(user.getJyId());
						if (jy != null) {
							User sendUser = jy.getSendUser();
							if (sendUser.equals(user)) {
								sendUser = jy.getAcceptUser();
							}
							// 存储交易内容
							ConcurrentHashMap<User, Userbag> map = jy.getJycontentMap();
							ConcurrentHashMap<User, Integer> jyMoney = jy.getJyMoney();
							if (map == null) {
								ConcurrentHashMap<User, Userbag> jycontentMap = new ConcurrentHashMap<>();
								jycontentMap.put(user, userbag);
								jy.setJycontentMap(jycontentMap);
							} else {
								map.put(user, userbag);
							}
							if (jyMoney == null) {
								ConcurrentHashMap<User, Integer> concurrentHashMap = new ConcurrentHashMap<>();
								concurrentHashMap.put(user, Integer.valueOf(msg[1]));
								jy.setJyMoney(concurrentHashMap);
							} else {
								jyMoney.put(user, Integer.valueOf(msg[1]));
							}
							// 改变状态
							user.getAndSetjyFlag(user, 2);
							Channel channel = IOsession.userchMp.get(sendUser);
							if (userbag.getIsadd() == 0) {
								Zb zb = IOsession.zbMp.get(userbag.getGid());
								SendMsg.send(user.getNickname() + "---装备:" + zb.getName() + "---金币:" + msg[1],channel);
							} else {
								Yaopin yaopin = IOsession.yaopinMp.get(userbag.getGid());
								SendMsg.send(
										user.getNickname() + "---药品:" + yaopin.getName() + "---金币:" + msg[1],channel);
							}
						}
						flag = true;
						break;
					}
				}
				if (flag == false) {
					SendMsg.send("物品不存在，请重新放入",ch);
				}
			} else {
				Jy jy = IOsession.jyMap.get(user.getJyId());
				if (jy != null) {
					User sendUser = jy.getSendUser();
					if (sendUser.equals(user)) {
						sendUser = jy.getAcceptUser();
					}
					// 存储交易内容
					ConcurrentHashMap<User, Integer> jyMoney = jy.getJyMoney();
					if (jyMoney == null) {
						ConcurrentHashMap<User, Integer> concurrentHashMap = new ConcurrentHashMap<>();
						concurrentHashMap.put(user, Integer.valueOf(msg[1]));
						jy.setJyMoney(concurrentHashMap);
					} else {
						jyMoney.put(user, Integer.valueOf(msg[1]));
					}
					// 改变状态
					user.getAndSetjyFlag(user, 2);
					Channel channel = IOsession.userchMp.get(sendUser);
					SendMsg.send(user.getNickname() + "---金币:" + msg[1],channel);
				}
			}
		} else {
			SendMsg.send("指令错误",ch);
		}
	}

	public void exchange(Jy jy, User acceptUser, User sendUser) {
		ConcurrentHashMap<User, Userbag> jycontentMap = jy.getJycontentMap();
		ConcurrentHashMap<User, Integer> jyMoney = jy.getJyMoney();
		List<Userbag> acceptUseList = IOsession.userBagMp.get(acceptUser);
		List<Userbag> sendUserList = IOsession.userBagMp.get(sendUser);
		if (jycontentMap != null) {
			Userbag userbag = jycontentMap.get(acceptUser);
			if(userbag!=null) {
			if (userbag.getIsadd() == 0) {
				Zb zb = IOsession.zbMp.get(userbag.getGid());
				Integer njd = userbag.getNjd();
				// 移除
				acceptUseList.remove(userbag);
				// 入包
				RpgUtil.putZbWithNJD(sendUser, zb, njd);
			} else {
				Yaopin yaopin = IOsession.yaopinMp.get(userbag.getGid());
				if (userbag.getNumber() == 1) {
					acceptUseList.remove(userbag);
				} else {
					userbag.setNumber(userbag.getNumber() - 1);
				}
				RpgUtil.putYaopin(sendUser, yaopin);
			}
		}
		}
		Integer integer = jyMoney.get(acceptUser);
		Integer integer2 = jyMoney.get(sendUser);
		acceptUser.getAndSetMoney(acceptUser, acceptUser.getMoney() - integer + integer2);
	}

	private void acceptJy(User user, Channel ch, ChannelGroup group, String[] msg) {
		if (msg.length == 3) {
			Jy jy = IOsession.jyMap.get(msg[2]);
			if (jy != null) {
				lock.lock();
				try {
					User user2 = jy.getSendUser();
					if (user2.getJyFlag() == 0) {
						user2.getAndSetjyFlag(user2, 1);
						user2.getAndSetjySendFlag(user2, 0);
						user.getAndSetjySendFlag(user, 0);
						user.getAndSetjyFlag(user, 1);
						user.setJyId(jy.getId());
						jy.setAcceptUser(user);
						SendMsg.send("进入交易状态",ch);
						Channel channel = IOsession.userchMp.get(user2);
						SendMsg.send("和" + user.getNickname() + "开始交易",channel);
						TaskManage.checkTaskCompleteBytaskid(user, 9);
					} else if (user2.getJySendFlag() == 0) {
						SendMsg.send("交易已过期",ch);
					} else {
						SendMsg.send("对方正在交易中",ch);
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					lock.unlock();
				}
			} else {
				SendMsg.send("交易单号不存在",ch);
			}
		} else {
			SendMsg.send("指令错误",ch);
		}
	}

	private void sendJy(User user, Channel ch, ChannelGroup group, String[] msg) {
		if (user.getJySendFlag() == 0) {
			if (IOsession.mp != null) {
				for (User user2 : IOsession.mp.values()) {
					if (msg[1].equals(user2.getNickname())) {
						if (user2.getJyFlag() == 1) {
							SendMsg.send("对方正在交易中,请稍后再试",ch);
						} else {
							user.getAndSetjySendFlag(user, 1);
							String jyId = UUID.randomUUID().toString();
							user.setJyId(jyId);
							Jy jy = new Jy();
							jy.setId(jyId);
							jy.setStartTime(System.currentTimeMillis());
							jy.setSendUser(user);
							IOsession.jyMap.put(jyId, jy);
							SendMsg.send("向" + user2.getNickname() + "-交易请求已发送",ch);
							Channel channel = IOsession.userchMp.get(user2);
							SendMsg.send(jyId + "--" + user.getNickname() + "请求跟你交易",channel);
							TaskManage.checkTaskCompleteBytaskid(user, 9);
						}
					}
				}
			}
		} else {
			SendMsg.send("操作频繁，请稍后再试",ch);
		}
	}

}
