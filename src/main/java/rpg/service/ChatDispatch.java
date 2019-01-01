package rpg.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.mysql.jdbc.EscapeTokenizer;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import rpg.pojo.EmailRpg;
import rpg.pojo.User;
import rpg.pojo.Userbag;
import rpg.pojo.Yaopin;
import rpg.pojo.Zb;
import rpg.session.IOsession;
import rpg.util.RpgUtil;
import rpg.util.SendMsg;

/**
 * 聊天功能
 * 
 * @author ljq
 *
 */
@Component
public class ChatDispatch {

	// 全服聊天
	public void chatAll(User user, Channel ch, ChannelGroup group, String msg1) {
		String[] msg = msg1.split("\\s+");
		if (msg.length == 2) {
			for (Channel channel : group) {
				SendMsg.send("全服喇叭----" + user.getNickname() + ":" + msg[1],channel);
			}
		} else {
			SendMsg.send("指令错误",ch);
		}
	}

	// 私聊
	public void chat(User user, Channel ch, ChannelGroup group, String msg1) {
		String[] msg = msg1.split("\\s+");
		if (msg.length == 3) {
			if (IOsession.mp != null) {
				for (User user2 : IOsession.mp.values()) {
					if (msg[1].equals(user2.getNickname())) {
						SendMsg.send(user.getNickname() + ":" + msg[2],ch);
						Channel channel = IOsession.userchMp.get(user2);
						SendMsg.send(user.getNickname() + ":" + msg[2],channel);
					}
				}
			}
		} else {
			SendMsg.send("错误指令",ch);
		}
	}

	// 发送和提取邮件
	public void Email(User user, Channel ch, ChannelGroup group, String msg1) {
		String[] msg = msg1.split("\\s+");
		if (msg.length == 3) {
			if (IOsession.alluserEmail.get(msg[1]) != null) {
				List<Userbag> userbagList = IOsession.userBagMp.get(user);
				for (Userbag wp : userbagList) {
					// 找到物品
					if (msg[2].equals(wp.getId())) {
						EmailRpg emailRpg = new EmailRpg();
						String emailRpgId = UUID.randomUUID().toString();
						emailRpg.setId(emailRpgId);
						emailRpg.setFujian(wp);
						emailRpg.setUser(user);
						// 移除物品
						Yaopin yaopin = IOsession.yaopinMp.get(wp.getGid());
						if (yaopin != null) {
							wp.setNumber(wp.getNumber() - 1);
						} else {
							userbagList.remove(wp);
						}
						// 存储邮件内容
						ArrayList<EmailRpg> list = IOsession.alluserEmail.get(msg[1]);
						list.add(emailRpg);
//								System.out.println(list.get(0).getFujian().getUsername());
						SendMsg.send("发送邮件成功",ch);
						if (IOsession.mp != null) {
							for (User user2 : IOsession.mp.values()) {
								Channel channel = IOsession.userchMp.get(user2);
								if (IOsession.userchMp.get(user2) != null&&ch!=channel) {
									SendMsg.send("收到一封来自" + user.getNickname() + "邮件",channel);
								}
							}
						}
						break;
					}
				}
			} else {
				SendMsg.send("用户不存在",ch);
			}
		} else if (msg.length == 2) {
			ArrayList<EmailRpg> list = IOsession.alluserEmail.get(user.getNickname());
			EmailRpg emailRpg = list.get(Integer.valueOf(msg[1]));
			Zb zb = IOsession.zbMp.get(emailRpg.getFujian().getGid());
			Yaopin yaopin = IOsession.yaopinMp.get(emailRpg.getFujian().getGid());
			if (zb != null) {
				RpgUtil.putZb(user, zb);
				int index=Integer.valueOf(msg[1]);
				list.remove(index);
				SendMsg.send("提取邮件成功",ch);
			} else if (yaopin != null) {
				RpgUtil.putYaopin(user, yaopin);
				int index=Integer.valueOf(msg[1]);
				list.remove(index);
				SendMsg.send("提取邮件成功",ch);
			}
		} else {
			SendMsg.send("指令错误",ch);
		}
	}

	// 展示邮件
	public void showEmail(User user, Channel ch, ChannelGroup group, String msg1) {
		String[] msg = msg1.split("\\s+");
		if (msg.length == 1) {
			ArrayList<EmailRpg> list = IOsession.alluserEmail.get(user.getNickname());
			int i = 0;
			String zbmsg = "";
			String yaopinmsg = "";
			for (EmailRpg emailRpg : list) {
				Zb zb = IOsession.zbMp.get(emailRpg.getFujian().getGid());
				if (zb != null) {
					zbmsg += i + "---来自" + emailRpg.getUser().getNickname() + "-" + zb.getName() + "-耐久度："
							+ emailRpg.getFujian().getNjd();
					i++;
				}
			}
			for (EmailRpg emailRpg : list) {
				Yaopin yaopin = IOsession.yaopinMp.get(emailRpg.getFujian().getGid());
				if (yaopin != null) {
					yaopinmsg += i + "---来自" + emailRpg.getUser().getNickname() + "-" + yaopin.getName();
					i++;
				}
			}
			SendMsg.send(zbmsg + yaopinmsg,ch);
		} else {
			SendMsg.send("指令错误",ch);
		}
	}
}
