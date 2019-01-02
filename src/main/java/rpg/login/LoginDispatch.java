package rpg.login;

import java.net.SocketAddress;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import rpg.client.ClientMain;
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
public class LoginDispatch {
	@Autowired
	private Login login;

	public void dispatch(Channel ch, String arg1, SocketAddress address) {
		String[] msg = arg1.split("\\s+");
		if (msg.length > 2) {
			User user = login.login(msg[1], msg[2]);
			if (user == null) {
				SendMsg.send("账户或密码错误，请重新登陆:", ch);
			} else {
				User user2 = IOsession.nameMap.get(user.getNickname());
				if(user2!=null) {
					Channel channel = IOsession.userchMp.get(user2);
					SendMsg.send("你已在别处登陆，请重新登陆", channel);
					IOsession.mp.put(ch.remoteAddress(), user2);
					IOsession.userchMp.put(user2, ch);
				} else {
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
				String roleName="";
				if(user.getRoletype()==1) roleName="战士";
				else if(user.getRoletype()==2) roleName="牧师";
				else if(user.getRoletype()==3) roleName="法师";
				else if(user.getRoletype()==4) roleName="召唤师";
				SendMsg.send("000角色名-"+user.getNickname()+"-职业-"+roleName, ch);
				SendMsg.send("登陆成功，欢迎" + user.getNickname() + "进入游戏", ch);
			}
		} else {
			SendMsg.send("指令错误" + "\n", ch);
		}
	}
}
