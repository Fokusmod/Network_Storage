package ru.gb.storage.server;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import ru.gb.storage.common.message.*;

import java.awt.*;
import java.io.*;
import java.util.Arrays;

public class FirstServerHandler extends SimpleChannelInboundHandler<Message> {

    private RandomAccessFile randomAccessFile = null;

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("New active channel");
        TextMessage answer = new TextMessage();
        answer.setText("Successfully connection");
        ctx.writeAndFlush(answer);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws IOException {
        if (msg instanceof TextMessage) {
            TextMessage message = (TextMessage) msg;
            System.out.println("incoming text message: " + message.getText());
            ctx.writeAndFlush(msg);
        }
        if (msg instanceof DateMessage) {
            DateMessage message = (DateMessage) msg;
            System.out.println("incoming date message: " + message.getDate());
            ctx.writeAndFlush(msg);
        }
        if (msg instanceof AuthMessage) {
            AuthMessage message = (AuthMessage) msg;
            System.out.println("incoming auth message: " + message.getPassword() + " " + message.getPassword());
            ctx.writeAndFlush(msg);
        }
        if (msg instanceof FileRequestMessage) {
            FileRequestMessage frm = (FileRequestMessage) msg;
            final File file = new File(frm.getPath());
            randomAccessFile = new RandomAccessFile(file, "r");
            sendFile(ctx);
        }
    }

    private void sendFile(ChannelHandlerContext ctx) throws IOException {
        if (randomAccessFile != null) {
            final byte[] fileContent;
            final long available = randomAccessFile.length() - randomAccessFile.getFilePointer();
            if (available > 100 * 1024) {
                fileContent = new byte[100 * 1024];
            } else {
                fileContent = new byte[(int) available];
            }
            final FileContentMessage fileContentMessage = new FileContentMessage();
            fileContentMessage.setStartPosition(randomAccessFile.getFilePointer());
            randomAccessFile.read(fileContent);
            fileContentMessage.setContent(fileContent);
            final boolean last = randomAccessFile.getFilePointer() == randomAccessFile.length();

            fileContentMessage.setLastPosition(last);
            ctx.writeAndFlush(fileContentMessage).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if (!last) {
                        sendFile(ctx);
                    }
                }
            });
            if (last) {
                randomAccessFile.close();
                randomAccessFile = null;
            }
        }


    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        System.out.println("client disconnect");
    }


}

