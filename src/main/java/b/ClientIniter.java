package b;

import java.nio.charset.Charset;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

public class ClientIniter extends ChannelInitializer<SocketChannel> {

	@Override
	protected void initChannel(SocketChannel arg0) throws Exception {
		ChannelPipeline pipeline = arg0.pipeline();
//		pipeline.addLast("lineD",new LineBasedFrameDecoder(1024));
		
		pipeline.addLast("stringD", new StringDecoder(Charset.forName("GBK")));
//		pipeline.addLast("stringC", new StringEncoder(Charset.forName("GBK")));
		pipeline.addLast("lineBasedFrameDecoder",new LineBasedFrameDecoder(2048));
		pipeline.addLast("http", new HttpClientCodec());
		pipeline.addLast("chat", new RpgClientHandler());
	}

}
