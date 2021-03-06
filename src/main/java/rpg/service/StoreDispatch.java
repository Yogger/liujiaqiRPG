package rpg.service;

import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import rpg.configure.InstructionsType;
import rpg.configure.MsgSize;
import rpg.pojo.Store;
import rpg.pojo.User;
import rpg.pojo.Yaopin;
import rpg.pojo.Zb;
import rpg.session.IOsession;
import rpg.task.TaskManage;
import rpg.util.RpgUtil;
import rpg.util.SendMsg;

/**商店处理逻辑
 * @author ljq
 *
 */
@Component
public class StoreDispatch {
	public void store(User user, Channel ch, ChannelGroup group, String msgR) {
		String[] msg = msgR.split("\\s+");
		Store store = IOsession.STORE_SYSTEM;
		HashMap<Integer, Yaopin> yaopinMap = store.getYaopinMap();
		HashMap<Integer, Zb> zbMap = store.getZbMap();
		if (msg.length > MsgSize.MAX_MSG_SIZE_2.getValue()) {
			if (InstructionsType.BUY.getValue().equals(msg[1])) {
				if (StringUtils.isNumeric(msg[MsgSize.MSG_INDEX_2.getValue()])) {
					if (zbMap.get(Integer.valueOf(msg[MsgSize.MSG_INDEX_2.getValue()])) != null) {
						Zb zb = zbMap.get(Integer.valueOf(msg[2]));
						if (zb.getPrice() < user.getMoney()) {
							user.setMoney(user.getMoney() - zb.getPrice());
							RpgUtil.putZb(user, zb);
							SendMsg.send("购买" + zb.getName() + "成功", ch);
							TaskManage.checkTaskCompleteBytaskidWithzb(user, 4, zb);
						} else {
							SendMsg.send("金币不足，购买失败", ch);
						}
					} else if (yaopinMap.get(Integer.valueOf(msg[MsgSize.MSG_INDEX_2.getValue()])) != null) {
						Yaopin yaopin = yaopinMap.get(Integer.valueOf(msg[2]));
						if (yaopin.getPrice() < user.getMoney()) {
							user.setMoney(user.getMoney() - yaopin.getPrice());
							RpgUtil.putYaopin(user, yaopin);
							SendMsg.send("购买" + yaopin.getName() + "成功", ch);
						} else {
							SendMsg.send("金币不足，购买失败", ch);
						}
					} else {
						SendMsg.send("物品不存在", ch);
					}
				} else {
					SendMsg.send("指令错误", ch);
				}
			} else if (InstructionsType.SELL.getValue().equals(msg[1])) {

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
				SendMsg.send(word, ch);
			} else {
				SendMsg.send("指令错误", ch);
			}
		}
	}
}
