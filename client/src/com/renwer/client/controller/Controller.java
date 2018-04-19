package com.renwer.client.controller;

import com.renwer.client.Main;
import com.renwer.networkelements.Connection;
import com.renwer.networkelements.ConnectionListener;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class Controller implements ConnectionListener{

    private static final String IP_ADDRESS = "127.0.0.1";
    private static final int PORT = 8189;
    private static final String DEFAULT_NAME = "Anonymous";
    
    private Connection connection;

    @FXML
    private TextField messageField;
    @FXML
    private VBox messages;
    @FXML
    private VBox activeUsers;
    @FXML
    private ScrollPane messagePane;


    public Controller(){
        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/LoginForm.fxml"));
            AnchorPane pane = (AnchorPane) loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Sign In");
            dialogStage.initModality(Modality.WINDOW_MODAL);

            Scene scene = new Scene(pane);
            dialogStage.setScene(scene);

            LoginFormController controller = loader.getController();
            controller.setDialogStage(dialogStage);

            dialogStage.showAndWait();
            PressedButton command = controller.getCommand();

            if(command == PressedButton.OK){
                String userName = controller.getUserName();
                this.connection = new Connection(this, IP_ADDRESS, PORT, userName);
            }else if(command == PressedButton.ANONYMOUS){
                this.connection = new Connection(this, IP_ADDRESS, PORT, DEFAULT_NAME);
            }
        }catch(IOException e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void initialize(){
        connection.init();
    }

    @Override
    public void onConnectionReady(Connection connection) {
        connection.sendString("TYPE:REGISTER USERNAME:" + connection.getUserName());


        addMessageToChat("Connected");
    }

    @Override
    public void onStringReceived(Connection connection, String string) {
        System.out.println("Received message: " + string);
        if(string.startsWith("TYPE:REGISTER")){
            String userName = string.substring(string.indexOf(':', string.indexOf("USERNAME:")) + 1);

            connection.setUserName(userName);
            addMessageToChat("Connected as " + userName);
        }else {
            addMessageToChat(string);
        }
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
