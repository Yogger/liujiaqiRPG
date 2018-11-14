package rpg.service;

import java.util.LinkedList;

import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import rpg.area.Area;
import rpg.pojo.Npc;
import rpg.pojo.User;

/**
 * 对话处理器
 * 
 * @author ljq
 *
 */
@Component("talkDispatch")
public class TalkDispatch {
	public void talk(User user, Channel ch, ChannelGroup group, String msgR) {
		String[] msg = msgR.split("\\s+");
		Integer id = user.getAreaid();
		LinkedList<Npc> npcList = Area.sceneList.get(id).getNpcList();
		for (Npc npc : npcList) {
			if(npc.getName().equals(msg[1])) {
				ch.writeAndFlush(npc.talk());
				return;
			}
		}
		ch.writeAndFlush("找不到该Npc");
	}
}
