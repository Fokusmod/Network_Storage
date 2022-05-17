package ru.gb.storage.client;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import ru.gb.storage.common.message.FileContentMessage;
import ru.gb.storage.common.message.Message;
import ru.gb.storage.common.message.TextMessage;

import java.io.IOException;
import java.io.RandomAccessFile;

public class ChannelHandler extends SimpleChannelInboundHandler<Message> {

    private static Network network;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        if(msg instanceof TextMessage) {
            network.sendRequestAuth(msg);
        }
        if (msg instanceof FileContentMessage) {
            System.out.println(((FileContentMessage) msg).getStartPosition());

            FileContentMessage fcm = (FileContentMessage) msg;
            try (RandomAccessFile randomAccessFile = new RandomAccessFile(network.correctlyPathDownload, "rw")) {
                randomAccessFile.seek(fcm.getStartPosition());
                randomAccessFile.write(fcm.getContent());
                if (fcm.isLastPosition()) {
                    network.controller.refreshUsersDirectory();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void setNetwork(Network network) {
        ChannelHandler.network = network;
    }

}
