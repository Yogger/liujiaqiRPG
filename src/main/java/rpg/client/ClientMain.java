package rpg.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.FixedRecvByteBufAllocator;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class ClientMain {
	private String host;
	private int port;
	private boolean stop = false;

	public ClientMain(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public static void main(String[] args) throws IOException {
		new ClientMain("127.0.0.1",8080).run();
	}

	public void run() throws IOException {
		EventLoopGroup worker = new NioEventLoopGroup();
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.group(worker);
		bootstrap.channel(NioSocketChannel.class);
		bootstrap.option(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(2048));
		bootstrap.option(ChannelOption.TCP_NODELAY, true).option(ChannelOption.SO_KEEPALIVE, true);
		bootstrap.handler(new ClientIniter());

		try {
			Channel channel = bootstrap.connect(host, port).sync().channel();
			System.out.println("请选择指令");
			System.out.println("login、登陆 regist、注册");
			System.out.println("格式：login username psw");
			System.out.println("格式：regist username psw psw");
			while (true) {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(System.in));
				String input = reader.readLine();
				if (input != null) {
					if ("quit".equals(input)) {
						System.exit(1);
					}
					channel.writeAndFlush(input);
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public boolean isStop() {
		return stop;
	}

	public void setStop(boolean stop) {
		this.stop = stop;
	}

}
