package rpg.client;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import rpg.util.SendMsg;

/**
 * 客户端处理业务逻辑
 * @author ljq
 *
 */
public class RpgClientHandler extends ChannelHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg3) throws Exception {
		String msg1 = (String) msg3;
		Channel channel = ctx.channel();
		if ("心跳".equals(msg1)) {
			SendMsg.send("心跳", channel);
		} else {
			if (msg1.length() > 3) {
				String msg = msg1.substring(0, 3);
				if ("001".equals(msg)) {
					String string = msg1.substring(3);
					Jm.printMsg(string, Jm.jTextArea2);
				} else if ("002".equals(msg)) {
					String string = msg1.substring(3);
					Jm.printMsg(string, Jm.jTextArea3);
					System.out.println(string);
				} else if ("003".equals(msg)) {
					String string = msg1.substring(3);
					Jm.printMsg(string, Jm.jTextArea4);
					System.out.println(string);
				} else if ("000".equals(msg)) {
					String string = msg1.substring(3);
					ClientMain.jm1.setTitle(string);
				} else {
					Jm.printMsg(msg1, Jm.jTextArea);
					System.out.println(msg1);
				}
			} else {
				if (msg1 != "") {
					Jm.printMsg(msg1, Jm.jTextArea);
					System.out.println(msg1);
				}

			}
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		System.err.println("客户端关闭3");
		Channel channel = ctx.channel();
		cause.printStackTrace();
		if (channel.isActive()) {
			System.err.println("simpleclient" + channel.remoteAddress() + "异常");
		}
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		new ClientMain("127.0.0.1", 8080).reConnectServer();
	}
}
