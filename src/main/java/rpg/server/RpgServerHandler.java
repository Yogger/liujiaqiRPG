package rpg.server;

import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.concurrent.GlobalEventExecutor;
import rpg.client.ClientMain;
import rpg.login.LoginDispatch;
import rpg.login.RegistDispatch;
import rpg.pojo.User;
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
import rpg.session.OffineDispatch;
import rpg.task.TaskfunctionDispatch;
import rpg.util.SendMsg;

@Sharable
@Component("rpgServerHandler")
public class RpgServerHandler extends ChannelHandlerAdapter {

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
	private OffineDispatch offineDispatch;
	@Autowired
	private FriendDispatch friendDispatch;

	// 客户端超时次数
	private Map<ChannelHandlerContext, Integer> clientOvertimeMap = new ConcurrentHashMap<>();
	private final int MAX_OVERTIME = 3; // 超时次数超过该值则注销连接

	// 存储连接进来的玩家
	public static final ChannelGroup group = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

	// 客户端和服务端建立连接，并且告诉每个客户端
	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		Channel channel = ctx.channel();
		// for (Channel ch : group) {
		// ch.writeAndFlush("[" + channel.remoteAddress() + "] " + "is comming");
		// }
		group.add(channel);
		// int size = group.size();
		// System.out.println(size);
	}

	// 客户端断开连接
	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		Channel channel = ctx.channel();
		// for (Channel ch : group) {
		// ch.writeAndFlush("[" + channel.remoteAddress() + "] " + "is exit");
		// }
		group.remove(channel);
	}

	// 连接处于活跃状态
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		// Channel channel = ctx.channel();
		// System.out.println("[" + channel.remoteAddress() + "] " + "online");
	}

	// 客户端断开连接
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		Channel ch = ctx.channel();
		offineDispatch.groupOffine(ch);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		System.out.println("[" + ctx.channel().remoteAddress() + "]" + "exit the room");
		cause.printStackTrace();
		ctx.close().sync();
	}

	// 服务端处理客户端请求消息
	public void channelRead(ChannelHandlerContext arg0, Object arg) throws Exception {
		String arg1 = (String) arg;
		if (!arg1.equals("心跳")) {
			Channel channel = arg0.channel();
			// 遍历所有连接
			for (Channel ch : group) {
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
							SendMsg.send("指令错误,请重新输入" + "\n",ch);
							break;
						}
					}
					// 用户已登陆
					else {
						boolean ackstatus = IOsession.ackStatus.containsKey(ch.remoteAddress());
						String[] msg = arg1.split("\\s+");
						User user = IOsession.mp.get(address);
						if (msg.length > 0) {
							switch (msg[0]) {
							case "f":
								friendDispatch.friend(user, ch, group, arg1);
								break;
							case "pk":
								pkDispatch.pk(user, ch, group, arg1);
								break;
							case "task":
								taskfunctionDispatch.task(user, ch, group, arg1);
								break;
							case "email":
								chatDispatch.Email(user, ch, group, arg1);
								break;
							case "showemail":
								chatDispatch.showEmail(user, ch, group, arg1);
								break;
							case "chatall":
								chatDispatch.chatAll(user, ch, group, arg1);
								break;
							case "chat":
								chatDispatch.chat(user, ch, group, arg1);
								break;
							case "store":
								storeDispatch.store(user, ch, group, arg1);
								break;
							case "group":
								groupDispatch.group(user, ch, group, arg1);
								break;
							case "copy":
								copyDispatch.copy(user, ch, group, arg1);
								break;
							case "showgroup":
								groupDispatch.showgroup(user, ch, group, arg1);
								break;
							case "showbag":
								bagDispatch.showBag(user, ch, group);
								break;
							case "use":
								useGoods.use(user, ch, group, arg1);
								break;
							case "showzb":
								bagDispatch.showZb(user, ch, group);
								break;
							case "arrbag":
								bagDispatch.arrangebag(user, ch, group, arg1);
								break;
							case "tkff":
								bagDispatch.tkffZb(user, ch, group, arg1);
								break;
							case "wear":
								bagDispatch.wearzb(user, ch, group, arg1);
								break;
							case "fix":
								bagDispatch.fix(user, ch, group, arg1);
								break;
							default:
								// 普通战斗状态
								if (ackstatus && IOsession.ackStatus.get(ch.remoteAddress()) == 1) {
									ackDispatch.ack(user, ch, group, arg1);
								}
								// 副本战斗状态
								else if (ackstatus && IOsession.ackStatus.get(ch.remoteAddress()) == 2) {
									ackBossDispatch.ack(user, ch, group, arg1);
								}
								// 交易状态
								else if (user.getJyFlag() == 1 || user.getJyFlag() == 2) {
									jyDispatch.jyProcess(user, ch, group, arg1);
								}
								// 普通状态
								else {
									switch (msg[0]) {
									case "jy":
										jyDispatch.jy(user, ch, group, arg1);
										break;
									case "gh":
										ghDIspatch.gh(user, ch, group, arg1);
										break;
									case "move":
										moveDispatch.dispatch(ch, msg, user);
										break;
									case "aoi":
										aoiDispatch.aoi(user, ch, group);
										break;
									case "talk":
										talkDispatch.talk(user, ch, group, arg1);
										break;
									case "ack":
										IOsession.ackStatus.put(address, 1);
										ackDispatch.ack(user, ch, group, arg1);
										break;
									default:
										SendMsg.send("无效指令",ch);
										break;
									}
								}
								break;
							}
						}
					}
				}
				// 其他连接客户
				else {
					// ch.writeAndFlush(channel.remoteAddress() + "上线" + "\n");
				}
			}
		}
		resetReconnectTimes();
		// System.out.println("服务端收到心跳");
		clientOvertimeMap.remove(arg0);// 只要接受到数据包，则清空超时次数
	}

	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		// 心跳包检测读超时
		if (evt instanceof IdleStateEvent) {
			IdleStateEvent e = (IdleStateEvent) evt;
			Channel channel = ctx.channel();
			if (e.state() == IdleState.READER_IDLE) {
				System.err.println("客户端读超时");
				int overtimeTimes = clientOvertimeMap.getOrDefault(ctx, 0);
				if (overtimeTimes < MAX_OVERTIME) {
					SendMsg.send("心跳",channel);
					addUserOvertime(ctx);
				} else {
					ServerManager.ungisterUserContext(ctx);
				}
			}
		}
	}

	private void addUserOvertime(ChannelHandlerContext ctx) {
		int oldTimes = 0;
		if (clientOvertimeMap.containsKey(ctx)) {
			oldTimes = clientOvertimeMap.get(ctx);
		}
		clientOvertimeMap.put(ctx, (int) (oldTimes + 1));
	}

	public void resetReconnectTimes() {
		if (ClientMain.reconnectTimes > 0) {
			ClientMain.reconnectTimes = 0;
			System.err.println("断线重连成功");
		}
	}

}
