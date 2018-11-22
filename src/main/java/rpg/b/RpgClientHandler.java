package rpg.b;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class RpgClientHandler extends ChannelHandlerAdapter {

	private static final Object String = null;

	int cout=0;
	public void channelRead(ChannelHandlerContext ctx, Object msg1) throws Exception {
		String msg=(String)msg1;
		if(msg.equals("心跳")) {
			ctx.writeAndFlush("心跳");
		}else {
		System.out.println(msg);
		System.out.println(cout++);
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
