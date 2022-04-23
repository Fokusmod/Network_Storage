package ru.gb.storage.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import ru.gb.storage.client.Controller.MainSceneController;
import ru.gb.storage.common.handler.JsonDecoder;
import ru.gb.storage.common.handler.JsonEncoder;
import ru.gb.storage.common.message.Message;
import ru.gb.storage.common.message.TextMessage;

public class Client_UI extends Application {



    public static void main(String[] args) {
        Application.launch(args);
    }

    public void start(Stage stage) throws Exception {

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                Platform.exit();
                System.exit(0);
            }
        });

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/MainScene.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();


        stage.setTitle("NetworkStorage");
        stage.setResizable(true);
        stage.setAlwaysOnTop(false);
        //stage.getIcons(); todo
        stage.show();


    }


}
