package rpg.service;

import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import rpg.area.Area;
import rpg.pojo.User;
import rpg.session.IOsession;
import rpg.util.SendMsg;

/**
 * aoi指令处理器
 * 
 * @author ljq
 *
 */
@Component("aoiDispatch")
public class AoiDispatch {

	public void aoi(User user, Channel ch, ChannelGroup group) {
		Integer id = user.getAreaid();
		switch (id) {
		case 1:
			select(user, ch, group, id);
			break;
		case 2:
			select(user, ch, group, id);
			break;
		case 3:
			select(user, ch, group, id);
			break;
		case 4:
			select(user, ch, group, id);
			break;
		default:
			SendMsg.send("您在地图内吗" + "\n",ch);
			break;
		}
	}

	public void select(User user, Channel ch, ChannelGroup group, int id) {
		String string = Area.sceneList.get(id - 1).toString();
		SendMsg.send(string, ch);
		SendMsg.send("本角色:" + user.getNickname() + "  ", ch);
		for (Channel channel : group) {
			if (channel != ch) {
				if (IOsession.mp.get(channel.remoteAddress()) != null) {
					User user2 = IOsession.mp.get(channel.remoteAddress());
					if (user2.getAreaid() .equals(user.getAreaid()) ) {
						SendMsg.send("其他角色:" + user2.getNickname(), ch);
					}
				}
			}
		}
	}
}
