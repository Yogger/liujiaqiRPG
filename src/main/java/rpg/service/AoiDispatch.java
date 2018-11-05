package rpg.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import rpg.area.Born;
import rpg.area.ChenBao;
import rpg.area.Senlin;
import rpg.area.Village;
import rpg.pojo.User;

@Component("aoiDispatch")
public class AoiDispatch {

	@Autowired
	private Born born;
	@Autowired
	private Village village;
	@Autowired
	private Senlin senlin;
	@Autowired
	private ChenBao chenBao;

	public void aoi(User user, Channel ch) {
		Integer id = user.getAreaid();
		switch (id) {
		case 1:
			String string = born.toString();
			ch.writeAndFlush("本角色"+user.getNickname());
			ch.writeAndFlush(string + "\n");
			break;
		case 2:
			String string1 = village.toString();
			ch.writeAndFlush("本角色"+user.getNickname());
			ch.writeAndFlush(string1 + "\n");
			break;
		case 3:
			String string2 = senlin.toString();
			ch.writeAndFlush("本角色"+user.getNickname());
			ch.writeAndFlush(string2 + "\n");
			break;
		case 4:
			String string3 = chenBao.toString();
			ch.writeAndFlush("本角色"+user.getNickname());
			ch.writeAndFlush(string3 + "\n");
			break;
		default:
			ch.writeAndFlush("您在地图内吗" + "\n");
			break;
		}
	}
}
