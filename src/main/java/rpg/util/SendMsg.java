package rpg.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;

/**消息发送管理
 * @author ljq
 *
 */
public class SendMsg {
	public static void send(String s, Channel ch) {
		byte[] string = (s + System.getProperty("line.separator")).getBytes();
		// byte[] string = (s+"\n").getBytes();
		ByteBuf message = Unpooled.buffer(string.length);
		message.writeBytes(string);
		ch.writeAndFlush(message);
	}
}