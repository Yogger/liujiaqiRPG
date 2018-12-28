package rpg.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import rpg.data.dao.UserfriendMapper;
import rpg.pojo.User;
import rpg.pojo.Userfriend;
import rpg.pojo.UserfriendExample;
import rpg.pojo.UserfriendExample.Criteria;
import rpg.session.IOsession;
import rpg.task.TaskManage;

/**
 * 好友功能
 * 
 * @author ljq
 *
 */
@Component
public class FriendDispatch {
	
	@Autowired
	private UserfriendMapper userfriendMapper;

	public void friend(User user, Channel ch, ChannelGroup group, String msgR) {
		String[] msg = msgR.split("\\s+");
		if (msg.length == 3 && msg[1].equals("add")) {
			addFriend(user, ch, group, msg);
		} else if (msg.length == 2 && msg[1].equals("showsq")) {
			showsq(user, ch, group, msg);
		} else if (msg.length == 2 && msg[1].equals("show")) {
			show(user, ch, group, msg);
		} else if (msg.length == 3 && msg[1].equals("accept")) {
			accept(user, ch, group, msg);
		}
	}

	private void accept(User user, Channel ch, ChannelGroup group, String[] msg) {
		List<String> list = IOsession.friendMp.get(user.getNickname());
		boolean flag=false;
		for (String string : list) {
			if(string.equals(msg[2])) {
				Userfriend userfriend = new Userfriend();
				String name=user.getNickname();
				userfriend.setUsername(name);
				userfriend.setFriend(msg[2]);
				userfriendMapper.insert(userfriend);
				Userfriend userfriend2 = new Userfriend();
				userfriend2.setUsername(msg[2]);
				userfriend2.setFriend(name);
				userfriendMapper.insert(userfriend2);
				flag=true;
				list.remove(string);
				User user2 = IOsession.nameMap.get(msg[2]);
				Channel channel = IOsession.userchMp.get(user2);
				ch.writeAndFlush("添加好友成功\n");
				TaskManage.checkMoneyTaskCompleteBytaskid(user, 12);
				channel.writeAndFlush("你已经和"+name+"成为好友\n");
				TaskManage.checkMoneyTaskCompleteBytaskid(user2, 12);
				break;
			}
		}
		if(!flag) {
			ch.writeAndFlush("指令错误");
		}
	}

	private void show(User user, Channel ch, ChannelGroup group, String[] msg) {
		String nickname = user.getNickname();
		UserfriendExample example = new UserfriendExample();
		Criteria criteria = example.createCriteria();
		criteria.andUsernameEqualTo(nickname);
		List<Userfriend> list = userfriendMapper.selectByExample(example);
		String word = "-------好友列表-------\n";
		for (Userfriend userfriend : list) {
			word+=userfriend.getFriend()+"\n";
		}
		ch.writeAndFlush(word);
	}

	private void showsq(User user, Channel ch, ChannelGroup group, String[] msg) {
		List<String> list = IOsession.friendMp.get(user.getNickname());
		String word = "-------好友申请列表-------\n";
		for (String string : list) {
			word += string + "\n";
		}
		ch.writeAndFlush(word);
	}

	private void addFriend(User user, Channel ch, ChannelGroup group, String[] msg) {
		if (IOsession.mp != null) {
			for (User user2 : IOsession.mp.values()) {
				if (msg[2].equals(user2.getNickname())) {
					String username1 = user2.getNickname();
					List<String> list = new ArrayList<>();
					String username2 = user.getNickname();
					list.add(username2);
					IOsession.friendMp.put(username1, list);
					ch.writeAndFlush("好友申请已发送");
					Channel channel = IOsession.userchMp.get(user2);
					channel.writeAndFlush(user.getNickname() + "申请与您成为好友");
				}
			}
		}
	}
}
