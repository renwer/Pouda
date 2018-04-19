package com.renwer.client;

import com.renwer.networkelements.Connection;
import com.renwer.networkelements.ConnectionListener;
import com.renwer.server.Server;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class Controller implements ConnectionListener {

    public static final String IP_ADDRESS = "127.0.0.1";
    public static final int PORT = 8189;
    
    private Connection connection;

    @FXML
    private TextField messageField;
    @FXML
    private VBox messages;
    @FXML
    private VBox activeUsers;
    @FXML
    private ScrollPane messagePane;


    @FXML
    private void initialize() {
        try {
            this.connection = new Connection(this, IP_ADDRESS, PORT);
        }catch(IOException e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onConnectionReady(Connection connection) {
        addMessageToChat("Connected");
        //Server.connections.add(connection);

        for (Connection c : Server.connections) {
            Label temp = new Label();
            temp.setText(c.toString());
            temp.setStyle("-fx-padding: 5;");
            temp.setPrefWidth(170.0);
            temp.setPrefHeight(25.0);
            activeUsers.getChildren().add(temp);
            }
        }


    @Override
    public void onStringReceived(Connection connection, String string) {
        System.out.println("Received message: " + string);
        addMessageToChat(string);
    }

    @Override
    public void onAbortConnection(Connection connection) {
        addMessageToChat("Disconnected");
    }

    @Override
    public void onException(Connection connection, Exception e) {
        addMessageToChat("Got some problem: " + e.getMessage());
    }

    @FXML
    private void handleEnterButtonPressed(KeyEvent ke){
        if(ke.getCode() == KeyCode.ENTER){
            onSendButtonClicked();
        }
    }

    public void onSendButtonClicked() {
        if (!messageField.getText().equals("")) {
            String messageText = messageField.getText();
            connection.sendString(messageText);
            messageField.setText("");
        }
    }

    private void addMessageToChat(String message){
        if(message != null && message.length() != 0){
            Label messageLabel = new Label(message);
            messageLabel.setStyle("-fx-background-color: #d7f9c5; -fx-padding: 10px; -fx-background-insets: 5px; -fx-font-size: 15px; -fx-background-radius: 8px");

            //This is important. Doesnt work without this.
            Platform.runLater(() ->
                    messages.getChildren().add(messageLabel)
            );

            messages.heightProperty().addListener(observable -> messagePane.setVvalue(1D));
            messageField.requestFocus();
        }

    }

}
