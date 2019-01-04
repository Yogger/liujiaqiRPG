package rpg.client;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

/**
 * 配置netty客户端
 * @author ljq
 *
 */
public class ClientIniter extends ChannelInitializer<SocketChannel> {

	@Override
	protected void initChannel(SocketChannel arg0) throws Exception {
		ChannelPipeline pipeline = arg0.pipeline();
		pipeline.addLast("lineD", new LineBasedFrameDecoder(1024));
		pipeline.addLast(new StringDecoder());
		// pipeline.addLast("stringD", new StringDecoder(Charset.forName("GBK")));
		// pipeline.addLast("stringC", new StringEncoder(Charset.forName("GBK")));
		// pipeline.addLast("http", new HttpClientCodec());
		pipeline.addLast("chat", new RpgClientHandler());
	}

}
