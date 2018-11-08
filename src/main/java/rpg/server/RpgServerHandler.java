package rpg.server;

import java.net.SocketAddress;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import rpg.login.LoginDispatch;
import rpg.login.RegistDispatch;
import rpg.pojo.User;
import rpg.service.AoiDispatch;
import rpg.service.MoveDispatch;
import rpg.service.TalkDispatch;
import rpg.session.IOsession;

@Sharable
@Component("rpgServerHandler")
public class RpgServerHandler extends SimpleChannelInboundHandler<String> {
	
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
	
	// 存储连接进来的玩家
	public static final ChannelGroup group = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

	// 客户端和服务端建立连接，并且告诉每个客户端
	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		Channel channel = ctx.channel();
		for (Channel ch : group) {
			ch.writeAndFlush("[" + channel.remoteAddress() + "] " + "is comming");
		}
		group.add(channel);
//		int size = group.size();
//		System.out.println(size);
	}

	// 客户端断开连接
	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		Channel channel = ctx.channel();
		for (Channel ch : group) {
			ch.writeAndFlush("[" + channel.remoteAddress() + "] " + "is comming");
		}
		group.remove(channel);
	}

	// 连接处于活跃状态
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
//		Channel channel = ctx.channel();
//		System.out.println("[" + channel.remoteAddress() + "] " + "online");
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
//		Channel channel = ctx.channel();
//		System.out.println("[" + channel.remoteAddress() + "] " + "offline");
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		System.out.println("[" + ctx.channel().remoteAddress() + "]" + "exit the room");
		cause.printStackTrace();
		ctx.close().sync();
	}

	// 服务端处理客户端请求消息
	@Override

	protected void messageReceived(ChannelHandlerContext arg0, String arg1) throws Exception {
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
						ch.writeAndFlush("指令错误,请重新输入" + "\n");
						break;
					}
				}
				// 用户已登陆
				else {
					String[] msg = arg1.split("\\s+");
					User user = IOsession.mp.get(address);
					switch (msg[0]) {
					case "move":
						moveDispatch.dispatch(ch, msg, user);
						break;
					case "aoi":
						aoiDispatch.aoi(user, ch,group);
						break;
					case "talk":
						talkDispatch.talk(user, ch, group, arg1);
						break;
					default:
						ch.writeAndFlush("无效指令");
						break;
					}
				}
			}
			// 其他连接客户
			else {
//				ch.writeAndFlush(channel.remoteAddress() + "上线" + "\n");
			}
		}
	}
}
