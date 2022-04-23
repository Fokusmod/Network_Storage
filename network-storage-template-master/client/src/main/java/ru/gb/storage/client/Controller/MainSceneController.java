package ru.gb.storage.client.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import ru.gb.storage.client.Network;
import ru.gb.storage.common.message.AuthMessage;

import java.net.URL;
import java.util.ResourceBundle;

public class MainSceneController implements Initializable {

    public String message = "";
    public VBox authBox;
    public Label authLabel;
    private Network network;
    public TextField loginField;
    public PasswordField passwordField;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        network = new Network();
        network.start();
        network.get(this); // Передача ссылки для нетворка
    }

    public void sendAuth(ActionEvent actionEvent) {
        AuthMessage message = new AuthMessage();
        message.setLogin(loginField.getText());
        message.setPassword(passwordField.getText());
        network.authorization(message);
        passwordField.clear();

    }

    public void closeAuth() {
        authBox.setVisible(false);
    }

    public void errorAuth() {
        authLabel.setVisible(true);
    }


}

