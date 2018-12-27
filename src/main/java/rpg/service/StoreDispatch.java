package rpg.service;

import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import rpg.pojo.Store;
import rpg.pojo.User;
import rpg.pojo.Yaopin;
import rpg.pojo.Zb;
import rpg.session.IOsession;
import rpg.task.TaskManage;
import rpg.util.RpgUtil;

@Component
public class StoreDispatch {
	public void store(User user, Channel ch, ChannelGroup group, String msgR) {
		String[] msg = msgR.split("\\s+");
		Store store = IOsession.store;
		HashMap<Integer, Yaopin> yaopinMap = store.getYaopinMap();
		HashMap<Integer, Zb> zbMap = store.getZbMap();
		if (msg.length > 2) {
			if (msg[1].equals("buy")) {
				if (StringUtils.isNumeric(msg[2])) {
					if (zbMap.get(Integer.valueOf(msg[2])) != null) {
						Zb zb = zbMap.get(Integer.valueOf(msg[2]));
						if (zb.getPrice() < user.getMoney()) {
							user.setMoney(user.getMoney() - zb.getPrice());
							RpgUtil.putZb(user, zb);
							ch.writeAndFlush("购买" + zb.getName() + "成功");
							TaskManage.checkTaskCompleteBytaskidWithzb(user, 4, zb);
						} else {
							ch.writeAndFlush("金币不足，购买失败");
						}
					} else if (yaopinMap.get(Integer.valueOf(msg[2])) != null) {
						Yaopin yaopin = yaopinMap.get(Integer.valueOf(msg[2]));
						if (yaopin.getPrice() < user.getMoney()) {
							user.setMoney(user.getMoney() - yaopin.getPrice());
							RpgUtil.putYaopin(user, yaopin);
							ch.writeAndFlush("购买" + yaopin.getName() + "成功");
						} else {
							ch.writeAndFlush("金币不足，购买失败");
						}
					} else {
						ch.writeAndFlush("物品不存在");
					}
				} else {
					ch.writeAndFlush("指令错误");
				}
			} else if (msg[1].equals("sell")) {

			}
		} else {
			String word = "";
			if (msg.length == 1) {
				word += store.getName() + "\n" + "-----装备-----" + "\n";
				for (Zb zb : zbMap.values()) {
					String level = "";
					for (int i = 0; i < zb.getLevel(); i++) {
						level += "★";
					}
					if (zb.getType() == 1) {
						word += "编号:" + zb.getId() + "-名字:" + zb.getName() + "-等级:" + level + "-攻击力:" + zb.getAck()
								+ "-价钱:" + zb.getPrice() + "耐久度：" + zb.getNjd() + "\n";
					} else if (zb.getType() == 2) {
						word += "编号:" + zb.getId() + "-名字:" + zb.getName() + "-等级:" + level + "-防御力:" + zb.getAck()
								+ "-价钱:" + zb.getPrice() + "耐久度：" + zb.getNjd() + "\n";
					}
				}
				word += "-----药品-----" + "\n";
				for (Yaopin yaopin : yaopinMap.values()) {
					word += "编号:" + yaopin.getId() + "-名字:" + yaopin.getName() + "-价钱:" + yaopin.getPrice();
				}
				ch.writeAndFlush(word);
			} else {
				ch.writeAndFlush("指令错误");
			}
		}
	}
}
