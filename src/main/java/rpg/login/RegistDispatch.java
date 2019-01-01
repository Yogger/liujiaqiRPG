package rpg.login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import rpg.util.SendMsg;

/**
 * 注册处理器
 * 
 * @author ljq
 *
 */
@Component("RegistDispatch")
public class RegistDispatch {

	@Autowired
	private Regist regist;

	public void dispatch(Channel ch, String arg1) {
		String[] msg = arg1.split("\\s+");
		if (msg.length > 3) {
			boolean b = regist.regist(msg[1], msg[2], msg[3]);
			if (b) {
				SendMsg.send("恭喜注册成功" + "\n", ch);
			} else {
				SendMsg.send("两次密码不一致" + "\n", ch);
			}
		} else {
			SendMsg.send("指令错误，重输：" + "\n", ch);
		}

	}
}
