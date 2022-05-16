package ru.gb.storage.client;

import com.sun.scenario.Settings;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ru.gb.storage.client.Controller.MainSceneController;
import ru.gb.storage.common.handler.JsonDecoder;
import ru.gb.storage.common.handler.JsonEncoder;
import ru.gb.storage.common.message.FileContentMessage;
import ru.gb.storage.common.message.FileRequestMessage;
import ru.gb.storage.common.message.Message;
import ru.gb.storage.common.message.TextMessage;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Network {
    private SocketChannel channel;
    protected MainSceneController controller;

    public Network(MainSceneController controller) {
        this.controller = controller;
    }

    private String pathServer = "C:\\Users\\Fokusmod\\Desktop\\";
    private String pathView = "C:\\Users\\Fokusmod\\Desktop";
    protected String correctlyPathDownload = "";
    private String correctNameFile = "";

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
                                        new ChannelHandler()
                                );
                            }
                        });
                ChannelHandler.setNetwork(this);
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

    public String getPathServer() {
        return pathServer;
    }

    public void authorization(Message message) {
        channel.writeAndFlush(message);
    }


    protected void sendRequestAuth(Message msg) {
        if (msg instanceof TextMessage) {
            TextMessage tm = (TextMessage) msg;
            if (tm.getText().equals("success")) {
                controller.closeAuth();
                createServerDirectory(pathServer);
                sendDirectoryOnView(pathView);
                sendUsersDirectory(pathServer);
            } else {
                controller.errorAuth();
            }
        }
    }

    private void createServerDirectory(String path) {
        String pathName = path;
        pathName += "\\";
        pathName += controller.loginField.getText();
        pathServer = pathName;
        File file = new File(pathName);
        file.mkdirs();
    }


    public void sendDirectoryOnView(String path) {
        File file = new File(path);
        File[] files = file.listFiles();
        ArrayList<String> list = new ArrayList<String>();
        list.add("//");
        for (File f : files) {
            if (f.exists())
                list.add(f.getName());
        }
        controller.serverList.setItems(FXCollections.observableArrayList(list));
    }

    public void upDirectory(String s) {
        String old = pathView;
        String newPath = old;
        newPath += "\\";
        newPath += s;
        File file = new File(newPath);
        if (!file.isFile()) {
            pathView = newPath;
            sendDirectoryOnView(pathView);
        }
    }

    public void backDirectory() {
        String path = pathView;
        path = path.substring(0, path.lastIndexOf("\\"));
        pathView = path;
        sendDirectoryOnView(pathView);
    }

    public String getPathView() {
        return pathView;
    }

    public void sendUsersDirectory(String path) {
        File file = new File(path);
        File[] files = file.listFiles();
        ArrayList<String> list = new ArrayList<>();
        for (File f : files) {
            if (f.exists())
                list.add(f.getName());
        }

        controller.clientList.setItems(FXCollections.observableArrayList(list));
    }

    public void sendFileOnClient(String s) {
        correctNameFile = s;
        String path = pathView;
        path += "\\";
        path += s;

        File file = new File(path);
        if (file.isFile()) {
            correctlyPathDownload = pathServer + "\\" + correctNameFile;
            FileRequestMessage message = new FileRequestMessage();
            message.setPath(path);
            channel.writeAndFlush(message);
        } else {
            System.out.println("This Directory");
        }


    }

    public void sendFileOnServer(String s) {
        String path = pathServer + "\\" + s;
        FileRequestMessage message = new FileRequestMessage();
        message.setPath(path);
        correctlyPathDownload = pathView + "\\" + correctNameFile;
        channel.writeAndFlush(message);
    }

    public void createFolder(String s) {
        File file = new File(pathView + "\\" + s);
        file.mkdir();
        sendDirectoryOnView(pathView);
    }


}

