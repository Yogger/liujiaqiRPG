package rpg.login;

import java.net.SocketAddress;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import rpg.configure.RoleType;
import rpg.pojo.User;
import rpg.pojo.UserAttribute;
import rpg.session.IOsession;
import rpg.util.SendMsg;

/**
 * 登陆处理器
 * 
 * @author ljq
 *
 */
@Component("loginDispatch")
@Slf4j
public class LoginDispatch {
	@Autowired
	private Login login;
	private ReentrantLock lock = new ReentrantLock();
	private static final int MSG_MAX_LENGTH = 2;
	
	public void dispatch(Channel ch, String arg1, SocketAddress address) {
		String[] msg = arg1.split("\\s+");
		if (msg.length > MSG_MAX_LENGTH) {
			User user = login.login(msg[1], msg[2]);
			if (user == null) {
				SendMsg.send("账户或密码错误，请重新登陆:", ch);
			} else {
				try {
					lock.lock();
					User user2 = IOsession.nameMap.get(user.getNickname());
					// 账号已登陆，顶号，转移状态
					if (user2 != null) {
						Channel channel = IOsession.userchMp.get(user2);
						SendMsg.send("你已在别处登陆，请重新登陆", channel);
						IOsession.mp.put(ch.remoteAddress(), user2);
						IOsession.mp.remove(channel.remoteAddress());
						IOsession.userchMp.put(user2, ch);
						IOsession.ackStatus.put(ch.remoteAddress(), IOsession.ackStatus.get(channel.remoteAddress()));
						IOsession.monsterMp.put(ch.remoteAddress(), IOsession.monsterMp.get(channel.remoteAddress()));
					}
					// 正常登陆
					else {
						IOsession.mp.put(address, user);
						IOsession.userchMp.put(user, ch);
						IOsession.nameMap.put(user.getNickname(), user);
						// 初始化属性
						UserAttribute attribute = new UserAttribute();
						attribute.setAck(105);
						attribute.setDef(50);
						IOsession.attMp.put(user, attribute);
						login.loadData(user);
					}
//				Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(new Refresh(), 0, 3000, TimeUnit.MILLISECONDS);
//				ch.writeAndFlush("登陆成功，欢迎" + user.getNickname() + "进入游戏" + "\n");
					String roleName = "";
					if (user.getRoletype() == RoleType.ZHANSHI.getValue()) {
						roleName = "战士";
					} else if (user.getRoletype() == RoleType.MUSHI.getValue()) {
						roleName = "牧师";
					} else if (user.getRoletype() == RoleType.FASHI.getValue()) {
						roleName = "法师";
					} else if (user.getRoletype() == RoleType.ZHAOHUANSHI.getValue()) {
						roleName = "召唤师";
					}
					SendMsg.send("000角色名-" + user.getNickname() + "-职业-" + roleName, ch);
					SendMsg.send("登陆成功，欢迎" + user.getNickname() + "进入游戏", ch);
					log.info("登陆游戏");
				} finally {
					lock.unlock();
				}
			}
		} else {
			SendMsg.send("指令错误" + "\n", ch);
		}
	}
}
