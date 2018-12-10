package rpg.client;

import javax.swing.JTextArea;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class RpgClientHandler extends SimpleChannelInboundHandler<String> {


	@Override
	protected void messageReceived(ChannelHandlerContext ctx, String msg1) throws Exception {
		if(msg1.equals("心跳")) {
			ctx.writeAndFlush("心跳");
		} else {
			if(msg1.length()>3) {
				String msg = msg1.substring(0, 3);
				if(msg.equals("001")) {
					String string = msg1.substring(3);
					jm.printMsg(string, jm.jTextArea2);
				} else{
					jm.printMsg(msg1,jm.jTextArea);
					System.out.println(msg1);
				}
			} else{
			jm.printMsg(msg1,jm.jTextArea);
			System.out.println(msg1);
		}
		}
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		System.err.println("客户端关闭3");
		Channel channel = ctx.channel();
		cause.printStackTrace();
		if(channel.isActive()){
			System.err.println("simpleclient"+channel.remoteAddress()+"异常");
		}
	}
	
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		new ClientMain("127.0.0.1",8080).reConnectServer();
	}
}
