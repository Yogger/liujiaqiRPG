package rpg.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import rpg.area.Area;
import rpg.configure.SceneType;
import rpg.data.dao.UserMapper;
import rpg.pojo.User;
import rpg.util.SendMsg;

/**
 * 移动
 * 
 * @author ljq
 *
 */
@Component("move")
public class Move {

	@Autowired
	private UserMapper userMapper;

	public void move(Channel ch, String msg, User user) {
//		Area area = new Area();
		if (SceneType.QI_SHI_ZHI_DI.getValue().equals(msg)) {
			if (Area.checkArea(msg, user.getAreaid()) > 0) {
				SendMsg.send("您已经进入起始之地", ch);
				user.setAreaid(1);
				userMapper.updateByPrimaryKey(user);
			} else {
				SendMsg.send("你不能跨场景，请重新输入指令", ch);
			}
		} else if (SceneType.SEN_LIN.getValue().equals(msg)) {
			if (Area.checkArea(msg, user.getAreaid()) > 0) {
				SendMsg.send("您已经进入森林", ch);
				user.setAreaid(3);
				userMapper.updateByPrimaryKey(user);
			} else {
				SendMsg.send("你不能跨场景，请重新输入指令", ch);
			}
		} else if (SceneType.CHEN_BAO.getValue().equals(msg)) {
			if (Area.checkArea(msg, user.getAreaid()) > 0) {
				SendMsg.send("您已经进入城堡", ch);
				user.setAreaid(4);
				userMapper.updateByPrimaryKey(user);
			} else {
				SendMsg.send("你不能跨场景，请重新输入指令", ch);
			}
		} else if (SceneType.CUN_ZI.getValue().equals(msg)) {
			if (Area.checkArea(msg, user.getAreaid()) > 0) {
				SendMsg.send("您已经进入村子", ch);
				user.setAreaid(2);
				userMapper.updateByPrimaryKey(user);
			} else {
				SendMsg.send("你不能跨场景，请重新输入指令", ch);
			}
		} else {
			SendMsg.send("无效指令", ch);
		}
	}
}
