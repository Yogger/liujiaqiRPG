package rpg.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import rpg.pojo.User;
import rpg.util.SendMsg;

/**
 * 移动处理器
 * @author ljq
 *
 */
@Component("moveDispatch")
public class MoveDispatch {
	
	@Autowired
	private Move move;
	
	public void dispatch(Channel ch, String []msg, User user) {
		if(msg.length>1) {
			move.move(ch, msg[1], user);
		} else {
			SendMsg.send("无效指令",ch);
		}
	}
}
