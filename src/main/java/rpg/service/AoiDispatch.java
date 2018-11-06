package rpg.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import rpg.area.Born;
import rpg.area.ChenBao;
import rpg.area.Senlin;
import rpg.area.Village;
import rpg.pojo.User;
import rpg.session.IOsession;

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

	public void aoi(User user, Channel ch, ChannelGroup group) {
		Integer id = user.getAreaid();
		switch (id) {
		case 1:
			select(user, ch, group);
			break;
		case 2:
			select(user, ch, group);
			break;
		case 3:
			select(user, ch, group);
			break;
		case 4:
			select(user, ch, group);
			break;
		default:
			ch.writeAndFlush("您在地图内吗" + "\n");
			break;
		}
	}

	public void select(User user, Channel ch, ChannelGroup group) {
		String string = born.toString();
		ch.writeAndFlush(string);
		ch.writeAndFlush("本角色:" + user.getNickname()+"  ");
		for (Channel channel : group) {
			if (channel != ch) {
				User user2 = IOsession.mp.get(channel.remoteAddress());
				if(user2.getAreaid()==user.getAreaid())//判断是否在一个场景
				ch.writeAndFlush("其他角色:" + user2.getNickname());
			}
		}
	}
}
