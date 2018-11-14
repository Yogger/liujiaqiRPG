package rpg.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class RpgClientHandler extends SimpleChannelInboundHandler<String> {


	@Override
	protected void messageReceived(ChannelHandlerContext ctx, String msg) throws Exception {
		System.out.println(msg);
	}

}
