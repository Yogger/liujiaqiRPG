package rpg.server.handle;

import java.net.SocketAddress;

import org.dom4j.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import rpg.login.LoginDispatch;
import rpg.login.RegistDispatch;
import rpg.pojo.User;
import rpg.server.RpgServerHandler;
import rpg.service.AckBossDispatch;
import rpg.service.AckDispatch;
import rpg.service.AoiDispatch;
import rpg.service.BagDispatch;
import rpg.service.ChatDispatch;
import rpg.service.CopyDispatch;
import rpg.service.FriendDispatch;
import rpg.service.GhDispatch;
import rpg.service.GroupDispatch;
import rpg.service.JyDispatch;
import rpg.service.MoveDispatch;
import rpg.service.PkDispatch;
import rpg.service.StoreDispatch;
import rpg.service.TalkDispatch;
import rpg.service.UseGoods;
import rpg.session.IOsession;
import rpg.task.TaskfunctionDispatch;
import rpg.util.SendMsg;

/**
 * 服务端接受指令处理逻辑
 * 
 * @author ljq
 *
 */
@Component
public class MsgHandle {
	@Autowired
	private GhDispatch ghDIspatch;
	@Autowired
	private TaskfunctionDispatch taskfunctionDispatch;
	@Autowired
	private JyDispatch jyDispatch;
	@Autowired
	private PkDispatch pkDispatch;
	@Autowired
	private StoreDispatch storeDispatch;
	@Autowired
	private LoginDispatch dispatch;
	@Autowired
	private RegistDispatch registDispatch;
	@Autowired
	private AoiDispatch aoiDispatch;
	@Autowired
	private MoveDispatch moveDispatch;
	@Autowired
	private TalkDispatch talkDispatch;
	@Autowired
	private AckDispatch ackDispatch;
	@Autowired
	private BagDispatch bagDispatch;
	@Autowired
	private UseGoods useGoods;
	@Autowired
	private GroupDispatch groupDispatch;
	@Autowired
	private CopyDispatch copyDispatch;
	@Autowired
	private AckBossDispatch ackBossDispatch;
	@Autowired
	private ChatDispatch chatDispatch;
	@Autowired
	private FriendDispatch friendDispatch;

	public void msgHandle(ChannelHandlerContext arg0, String arg1) throws Exception {
		ChannelGroup userGroup = RpgServerHandler.USER_GROUP;
		Channel channel = arg0.channel();
		// 遍历所有连接
		for (Channel ch : userGroup) {
			// 当前连接客户
			if (ch == channel) {
				// 获取session
				SocketAddress address = ch.remoteAddress();
				boolean key = IOsession.mp.containsKey(address);
				// 如果用户未登陆
				if (!key) {
					String[] type = arg1.split("\\s+");
					switch (type[0]) {
					case "login":
						dispatch.dispatch(ch, arg1, address);
						break;
					case "regist":
						registDispatch.dispatch(ch, arg1);
						break;
					default:
						SendMsg.send("指令错误,请重新输入" + "\n", ch);
						break;
					}
				}
				// 用户已登陆
				else {
					loginYesHandle(arg1, userGroup, ch, address);
				}
			}
			// 其他连接客户
			else {
				// ch.writeAndFlush(channel.remoteAddress() + "上线" + "\n");
			}
		}
	}

	/**
	 * 登陆成功状态消息处理
	 * 
	 * @param arg1
	 * @param userGroup
	 * @param ch
	 * @param address
	 * @throws DocumentException
	 */
	public void loginYesHandle(String arg1, ChannelGroup userGroup, Channel ch, SocketAddress address)
			throws DocumentException {
		boolean ackstatus = IOsession.ackStatus.containsKey(ch.remoteAddress());
		String[] msg = arg1.split("\\s+");
		User user = IOsession.mp.get(address);
		if (msg.length > 0) {
			msgMatching(arg1, userGroup, ch, address, ackstatus, msg, user);
		}
	}

	/**进行具体的命令匹配处理
	 * @param arg1
	 * @param userGroup
	 * @param ch
	 * @param address
	 * @param ackstatus
	 * @param msg
	 * @param user
	 * @throws DocumentException
	 */
	public void msgMatching(String arg1, ChannelGroup userGroup, Channel ch, SocketAddress address, boolean ackstatus,
			String[] msg, User user) throws DocumentException {
		switch (msg[0]) {
		case "f":
			friendDispatch.friend(user, ch, userGroup, arg1);
			break;
		case "pk":
			pkDispatch.pk(user, ch, userGroup, arg1);
			break;
		case "task":
			taskfunctionDispatch.task(user, ch, userGroup, arg1);
			break;
		case "email":
			chatDispatch.email(user, ch, userGroup, arg1);
			break;
		case "showemail":
			chatDispatch.showEmail(user, ch, userGroup, arg1);
			break;
		case "chatall":
			chatDispatch.chatAll(user, ch, userGroup, arg1);
			break;
		case "chat":
			chatDispatch.chat(user, ch, userGroup, arg1);
			break;
		case "store":
			storeDispatch.store(user, ch, userGroup, arg1);
			break;
		case "group":
			groupDispatch.group(user, ch, userGroup, arg1);
			break;
		case "copy":
			copyDispatch.copy(user, ch, userGroup, arg1);
			break;
		case "showgroup":
			groupDispatch.showgroup(user, ch, userGroup, arg1);
			break;
		case "showbag":
			bagDispatch.showBag(user, ch, userGroup);
			break;
		case "use":
			useGoods.use(user, ch, userGroup, arg1);
			break;
		case "showzb":
			bagDispatch.showZb(user, ch, userGroup);
			break;
		case "arrbag":
			bagDispatch.arrangebag(user, ch, userGroup, arg1);
			break;
		case "tkff":
			bagDispatch.tkffZb(user, ch, userGroup, arg1);
			break;
		case "wear":
			bagDispatch.wearzb(user, ch, userGroup, arg1);
			break;
		case "fix":
			bagDispatch.fix(user, ch, userGroup, arg1);
			break;
		default:
			// 普通战斗状态
			if (ackstatus && IOsession.ackStatus.get(ch.remoteAddress()) == 1) {
				ackDispatch.ack(user, ch, userGroup, arg1);
			}
			// 副本战斗状态
			else if (ackstatus && IOsession.ackStatus.get(ch.remoteAddress()) == 2) {
				ackBossDispatch.ack(user, ch, userGroup, arg1);
			}
			// 交易状态
			else if (user.getJyFlag() == 1 || user.getJyFlag() == 2) {
				jyDispatch.jyProcess(user, ch, userGroup, arg1);
			}
			// 普通状态
			else {
				ordinaryStatusHandle(arg1, userGroup, ch, address, msg, user);
			}
			break;
		}
	}

	/**登陆状态下普通情况处理
	 * @param arg1
	 * @param userGroup
	 * @param ch
	 * @param address
	 * @param msg
	 * @param user
	 */
	public void ordinaryStatusHandle(String arg1, ChannelGroup userGroup, Channel ch, SocketAddress address,
			String[] msg, User user) {
		switch (msg[0]) {
		case "jy":
			jyDispatch.jy(user, ch, userGroup, arg1);
			break;
		case "gh":
			ghDIspatch.gh(user, ch, userGroup, arg1);
			break;
		case "move":
			moveDispatch.dispatch(ch, msg, user);
			break;
		case "aoi":
			aoiDispatch.aoi(user, ch, userGroup);
			break;
		case "talk":
			talkDispatch.talk(user, ch, userGroup, arg1);
			break;
		case "ack":
			IOsession.ackStatus.put(address, 1);
			ackDispatch.ack(user, ch, userGroup, arg1);
			break;
		default:
			SendMsg.send("无效指令", ch);
			break;
		}
	}
}
