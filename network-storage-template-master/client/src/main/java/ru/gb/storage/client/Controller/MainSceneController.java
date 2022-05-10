package ru.gb.storage.client.Controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import ru.gb.storage.client.Network;
import ru.gb.storage.common.message.AuthMessage;

import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ResourceBundle;

public class MainSceneController implements Initializable {


    public ListView<String> serverList;
    public ListView<String> clientList;
    public Label name;
    public TextField path;
    public TextField newFolderField;
    public Pane newFolderPane;
    private Network network;
    public VBox authBox;
    public Label authLabel;
    public Pane mainBox;
    public TextField loginField;
    public PasswordField passwordField;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        network = new Network();
        network.getController(this); // Передача ссылки для нетворка
        network.start();
    }


    public void sendAuth(ActionEvent actionEvent) {
        AuthMessage message = new AuthMessage();
        message.setLogin(loginField.getText());
        message.setPassword(passwordField.getText());
        network.authorization(message);
        passwordField.clear();

    }


    public void closeAuth() {
        Platform.runLater(() -> {
            authBox.setVisible(false);
            mainBox.setVisible(true);
            name.setText(loginField.getText());
            path.setText(network.getPathView());
        });
    }

    public void refreshUsersDirectory() {
        Platform.runLater(() -> {
            network.sendUsersDirectory(network.getPathServer());
        });

    }

    public void errorAuth() {
        authLabel.setVisible(true);
    }



    public void sendDirectory(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 2) {
            if (serverList.getSelectionModel().getSelectedItem().equals("//")) {
                network.backDirectory();
                path.setText(network.getPathView());
            } else {
                String newDirrectory = serverList.getSelectionModel().getSelectedItem();
                network.upDirectory(newDirrectory);
                path.setText(network.getPathView());
            }
        }
    }


    public void download(ActionEvent actionEvent) {
        String path = serverList.getSelectionModel().getSelectedItem();
        if (path.equals("//")) {
            return;
        } else {
            network.sendFileOnClient(path);
        }

    }

    public void upload(ActionEvent actionEvent) {
        String path = clientList.getSelectionModel().getSelectedItem();
        network.sendFileOnServer(path);
    }

    public void newFolder(ActionEvent actionEvent) {
        newFolderPane.setVisible(true);
    }

    public void SendOkNewFolder(ActionEvent actionEvent) {
        String nameFolder = newFolderField.getText();
        network.createFolder(nameFolder);
        newFolderField.clear();
        newFolderPane.setVisible(false);
    }

    public void refresh(ActionEvent actionEvent) {
        network.sendDirectoryOnView(network.getPathView());
    }
}

