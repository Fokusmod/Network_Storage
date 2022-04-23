package ru.gb.storage.client.Controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import ru.gb.storage.client.Network;
import ru.gb.storage.common.message.AuthMessage;

import java.net.URL;
import java.util.ResourceBundle;

public class MainSceneController implements Initializable {

    public static String message = "";
    public static VBox AuthBox;
    private Network network;
    public TextField loginField;
    public PasswordField passwordField;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        network = new Network();
        network.start();


    }

    public void sendAuth(ActionEvent actionEvent) {
        AuthMessage message = new AuthMessage();
        message.setLogin(loginField.getText());
        message.setPassword(passwordField.getText());
        network.authorization(message);


    }

    public static void received() {
        if (!message.equals("success")) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            AuthBox.setVisible(false);
        }

    }


}

