package ru.gb.storage.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import ru.gb.storage.client.Controller.MainSceneController;
import ru.gb.storage.common.handler.JsonDecoder;
import ru.gb.storage.common.handler.JsonEncoder;
import ru.gb.storage.common.message.Message;
import ru.gb.storage.common.message.TextMessage;

public class Network {
    private SocketChannel channel; // для отправки сообщений с клиента на сервер


    public void start() {
        Thread t = new Thread(() -> {
            final NioEventLoopGroup group = new NioEventLoopGroup(1);
            try {
                Bootstrap bootstrap = new Bootstrap()
                        .group(group)
                        .channel(NioSocketChannel.class)
                        .option(ChannelOption.SO_KEEPALIVE, true)
                        .handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel ch) {
                                channel = ch;
                                ch.pipeline().addLast(
                                        new LengthFieldBasedFrameDecoder(1024 * 1024, 0, 3, 0, 3),
                                        new LengthFieldPrepender(3),
                                        new JsonDecoder(),
                                        new JsonEncoder(),
                                        new SimpleChannelInboundHandler<Message>() {
                                            @Override
                                            public void channelActive(ChannelHandlerContext ctx) throws Exception {
//                                            final FileRequestMessage frm = new FileRequestMessage();
//                                            frm.setPath("C:/Users/Fokusmod/Desktop/Win8.1_English_x64.iso");
//                                            ctx.writeAndFlush(frm);
                                            }

                                            @Override
                                            protected void channelRead0(ChannelHandlerContext ctx, Message msg) {

                                                sendRequestAuth(msg);

//

//                                            if (msg instanceof FileContentMessage) {
//                                                System.out.println("FileContentMessage " + ((FileContentMessage) msg).getStartPosition());
//                                                FileContentMessage fcm = (FileContentMessage) msg;
//                                                try (RandomAccessFile randomAccessFile = new RandomAccessFile("C:/Users/Fokusmod/Desktop/Test_Win8.1_English_x64.iso", "rw")) {
//                                                    randomAccessFile.seek(fcm.getStartPosition());
//                                                    randomAccessFile.write(fcm.getContent());
//                                                    if (fcm.isLastPosition()) {
//                                                        ctx.close();
//                                                    }
//                                                } catch (IOException e) {
//                                                    e.printStackTrace();
//                                                }
//                                            }
                                            }
                                        }
                                );
                            }
                        });

                System.out.println("Client started");
                Channel channel = bootstrap.connect("localhost", 9000).sync().channel();
                channel.closeFuture().sync();

            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                group.shutdownGracefully();
            }
        });
        t.start();

    }

    public void authorization(Message message) {
        channel.writeAndFlush(message);
    }

    public void sendRequestAuth(Message msg) {


        if (msg instanceof TextMessage) {
            TextMessage tm = (TextMessage) msg;
            if (tm.getText().equals("success")) {

            MainSceneController.message = tm.getText();

            } else {


            }
        }
    }

}
