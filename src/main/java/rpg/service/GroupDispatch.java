package rpg.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import rpg.configure.InstructionsType;
import rpg.configure.MsgSize;
import rpg.pojo.Group;
import rpg.pojo.User;
import rpg.session.IOsession;
import rpg.task.TaskManage;
import rpg.util.SendMsg;

/**
 * 组队
 * @author ljq
 *
 */
@Component
public class GroupDispatch {
	/**
	 * 组队邀请 指令：group 用户
	 * @param user
	 * @param ch
	 * @param group
	 * @param msgR
	 */
	public void group(User user, Channel ch, ChannelGroup group, String msgR) {
		String[] msg = msgR.split("\\s+");
		// 接受组队请求 指令：group yes 用户
		if (InstructionsType.YES.getValue().equals(msg[1])&&msg.length>1) {
			groupYes(user, ch, group, msgR);
		}
		// 拒绝组队请求 指令：group no 用户
		else if (InstructionsType.NO.getValue().equals(msg[1])&&msg.length>1) {
			groupNo(user, ch, group, msgR);
		} 
		else if(InstructionsType.DIV.getValue().equals(msg[1])&&msg.length>1) {
			groupDiv(user,ch,group,msgR);
		}
		else {
			if (IOsession.mp != null) {
				for (User user2 : IOsession.mp.values()) {
					if (msg[1].equals(user2.getNickname())) {
						if(user2.getGroupId()!=null) {
							SendMsg.send("该玩家已在队伍中",ch);
						} else {
						SendMsg.send("邀请-" + user2.getNickname() + "-组队请求已发送",ch);
						Channel channel = IOsession.userchMp.get(user2);
						SendMsg.send(user.getNickname() + "-邀请你组队",channel);
						SendMsg.send("group yes 用户-接受" + "  group no 用户-拒绝",channel);
						String groupId = UUID.randomUUID().toString();
						user.setGroupId(groupId);
						Group group2 = new Group();
						group2.setId(groupId);
						group2.setUser(user);
						ArrayList<User> list = new ArrayList<>();
						list.add(user);
						group2.setList(list);
						IOsession.userGroupMp.put(groupId, group2);
						TaskManage.checkTaskCompleteBytaskid(user, 7);
					}
					}
				}
			}
		}
	}

	private void groupDiv(User user, Channel ch, ChannelGroup group, String msgR) {
		String[] msg = msgR.split("\\s+");
		if(msg.length==MsgSize.MAX_MSG_SIZE_2.getValue()) {
			if(user.getGroupId()!=null) {
			Group group2 = IOsession.userGroupMp.get(user.getGroupId());
			if(group2.getUser().getNickname().equals(user.getNickname())) {
				SendMsg.send("队长不能退队",ch);
			}else {
				List<User> list = group2.getList();
				list.remove(user);
				user.setGroupId(null);
				for (User user3 : list) {
					if(user3!=user) {
					Channel channel = IOsession.userchMp.get(user3);
					SendMsg.send(user.getNickname()+"离开队伍",channel);
					}
				}
			}
			}else {
				SendMsg.send("你不在队伍中",ch);
			}
		} else {
			SendMsg.send("指令错误",ch);
		}
	}

	/**
	 * 展示队伍列表
	 * @param user
	 * @param ch
	 * @param group
	 * @param msgR
	 */
	public void showgroup(User user, Channel ch, ChannelGroup group, String msgR) {
		Group group2 = IOsession.userGroupMp.get(user.getGroupId());
		if (group2 != null) {
			List<User> list = group2.getList();
			SendMsg.send("队长" + group2.getUser().getNickname(),ch);
			SendMsg.send("队员：",ch);
			for (User user2 : list) {
				if(group2.getUser()!=user2) {
					SendMsg.send(user2.getNickname() + " ",ch);
				}
			}
		} else {
			SendMsg.send("不存在队伍",ch);
		}
	}

	private void groupNo(User user, Channel ch, ChannelGroup group, String msgR) {
		String[] msg = msgR.split("\\s+");
		if (msg.length == MsgSize.MAX_MSG_SIZE_3.getValue()) {
			if (IOsession.mp != null) {
				for (User user2 : IOsession.mp.values()) {
					if (msg[2].equals(user2.getNickname())) {
						Channel channel = IOsession.userchMp.get(user2);
						SendMsg.send("拒绝加入队伍成功",ch);
						SendMsg.send(user.getNickname() + "拒绝加入队伍",channel);
					}
				}
			}
		} else {
			SendMsg.send("指令错误",ch);
		}
	}

	private void groupYes(User user, Channel ch, ChannelGroup group, String msgR) {
		String[] msg = msgR.split("\\s+");
		if (msg.length == MsgSize.MAX_MSG_SIZE_3.getValue()) {
			if (IOsession.mp != null) {
				for (User user2 : IOsession.mp.values()) {
					if (msg[2].equals(user2.getNickname())) {
						Channel channel = IOsession.userchMp.get(user2);
						Group group2 = IOsession.userGroupMp.get(user2.getGroupId());
						if (group2 != null) {
							List<User> list = group2.getList();
							user.setGroupId(user2.getGroupId());
							list.add(user);
							SendMsg.send("你已进入" + user2.getNickname() + "队伍",ch);
							SendMsg.send(user.getNickname() + "进入队伍",channel);
							TaskManage.checkTaskCompleteBytaskid(user, 7);
						}
					}
				}
			}
		} else {
			SendMsg.send("指令错误",ch);
		}
	}
}
