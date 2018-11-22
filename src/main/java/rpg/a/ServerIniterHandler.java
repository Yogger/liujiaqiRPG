package rpg.a;

import java.nio.charset.Charset;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.timeout.IdleStateHandler;

@Component("serverIniterHandler")
public class ServerIniterHandler extends  ChannelInitializer<SocketChannel> {
	
	@Autowired
	private RpgServerHandler rpgServerHandler;
	
	@Override
	protected void initChannel(SocketChannel arg0) throws Exception {
		ChannelPipeline pipeline = arg0.pipeline();
//		pipeline.addLast("fdecoder",new LineBasedFrameDecoder(1024));
//		pipeline.addLast(new StringDecoder(Charset.forName("utf-8")));
//		pipeline.addLast("encode",new StringEncoder(Charset.forName("GBK")));
		pipeline.addLast(new LineBasedFrameDecoder(1024));
		pipeline.addLast(new StringDecoder());
//		pipeline.addLast("idleStateHandler", new IdleStateHandler(5, 0, 0));
		pipeline.addLast(rpgServerHandler);
		
	}

}
