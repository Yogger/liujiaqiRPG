package client;

import static client.PacketProto.Packet.newBuilder;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * @author xiaojianyu
 */
public class ClientHandler extends ChannelHandlerAdapter {


    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof PacketProto.Packet) {
            PacketProto.Packet packet = (PacketProto.Packet) msg;
            System.out.println("客户端收到：" + packet.getData());
        }
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("--- Server is inactive ---");
        // 10s 之后尝试重新连接服务器

//        System.out.println("5s 之后尝试重新连接服务器...");
//        int count = 0;
//        while (true) {
//            if(count ==5)break;
//            Thread.sleep(5 * 1000);
//            Client.doConnect();
//            count++;
//        }

    }


    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//        if (null != cause) cause.printStackTrace();
        if (null != ctx) ctx.close();
    }


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            // 不管是读事件空闲还是写事件空闲都向服务器发送心跳包
            sendHeartbeatPacket(ctx);
        }
    }

    /**
     * 发送心跳包
     */
    private void sendHeartbeatPacket(ChannelHandlerContext ctx) {
        PacketProto.Packet.Builder builder = newBuilder();
        builder.setPacketType(PacketProto.Packet.PacketType.HEARTBEAT);
        PacketProto.Packet packet = builder.build();
        ctx.writeAndFlush(packet);
    }
}
