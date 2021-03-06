package ru.gb.storage.server;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import ru.gb.storage.common.message.*;
import ru.gb.storage.server.Database.Database;

import java.io.*;
import java.sql.SQLException;

public class FirstServerHandler extends SimpleChannelInboundHandler<Message> {

    private RandomAccessFile randomAccessFile = null;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws SQLException, ClassNotFoundException {
        Database.connect();
        System.out.println("New active channel");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws IOException, ClassNotFoundException {

        if (msg instanceof AuthMessage) {
            AuthMessage message = (AuthMessage) msg;
            try {
                if (!Database.isConnected()) {
                    Database.connect();
                }

                if (Database.login(message.getLogin(), message.getPassword())) {
                    TextMessage textMessage = new TextMessage();
                    textMessage.setText("success");
                    ctx.writeAndFlush(textMessage);
                    Database.disconnect();
                } else {
                    TextMessage textMessage = new TextMessage();
                    textMessage.setText("Incorrect login or password");
                    ctx.writeAndFlush(textMessage);
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

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

