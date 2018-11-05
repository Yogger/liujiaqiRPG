package _01discard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

@Service("helloServerInitializer")
public class HelloServerInitializer extends ChannelInitializer<SocketChannel> {

    @Autowired
    private DiscardServerHandler discardServerHandler;
    
    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast(discardServerHandler);// demo1.discard
        // ch.pipeline().addLast(new
        // ResponseServerHandler());//demo2.echo
        // ch.pipeline().addLast(new
        // TimeServerHandler());//demo3.time
    }
}