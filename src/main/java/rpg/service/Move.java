package rpg.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import rpg.area.Area;
import rpg.data.dao.UserMapper;
import rpg.pojo.User;

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
	
	public  void move(Channel ch, String msg, User user) {
		Area area = new Area();
		if (msg.equals("起始之地")) {
			if (area.checkArea(msg, user.getAreaid()) > 0) {
				ch.writeAndFlush("您已经进入起始之地");
				user.setAreaid(1);
				userMapper.updateByPrimaryKey(user);
			} else {
				ch.writeAndFlush("你不能跨场景，请重新输入指令");
			}
		} else if (msg.equals("森林")) {
			if (area.checkArea(msg, user.getAreaid()) > 0) {
				ch.writeAndFlush("您已经进入森林");
				user.setAreaid(3);
				userMapper.updateByPrimaryKey(user);
			} else {
				ch.writeAndFlush("你不能跨场景，请重新输入指令");
			}
		} else if (msg.equals("城堡")) {
			if (area.checkArea(msg, user.getAreaid()) > 0) {
				ch.writeAndFlush("您已经进入城堡");
				user.setAreaid(4);
				userMapper.updateByPrimaryKey(user);
			} else {
				ch.writeAndFlush("你不能跨场景，请重新输入指令");
			}
		} else if (msg.equals("村子")) {
			if (area.checkArea(msg, user.getAreaid()) > 0) {
				ch.writeAndFlush("您已经进入村子");
				user.setAreaid(2);
				userMapper.updateByPrimaryKey(user);
			} else {
				ch.writeAndFlush("你不能跨场景，请重新输入指令");
			}
		} else {
			ch.writeAndFlush("无效指令");
		}
	}
}
