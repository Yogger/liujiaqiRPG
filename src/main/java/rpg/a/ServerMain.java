package rpg.a;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.FixedRecvByteBufAllocator;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

@Service("serverMain")
public class ServerMain {

	@Autowired
	private ServerIniterHandler serverIniterHandler;

	private final int port = 8080;

	public void run() {
		EventLoopGroup acceptor = new NioEventLoopGroup();
		EventLoopGroup worker = new NioEventLoopGroup();
		ServerBootstrap bootstrap = new ServerBootstrap();
		bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
		bootstrap.group(acceptor, worker);
		bootstrap.channel(NioServerSocketChannel.class);
		bootstrap.childOption(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(2048));//处理字符串过长的问题
		bootstrap.childOption(ChannelOption.TCP_NODELAY, true).childOption(ChannelOption.SO_KEEPALIVE, true);
		bootstrap.childHandler(serverIniterHandler);
		try {
			Channel channel = bootstrap.bind(port).sync().channel();
			System.out.println("server strart running in port:" + port);
			channel.closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			acceptor.shutdownGracefully();
			worker.shutdownGracefully();
		}
	}
}
