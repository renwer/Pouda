package com.renwer.client.controller;

import com.renwer.client.Main;
import com.renwer.networkelements.Connection;
import com.renwer.networkelements.ConnectionListener;
import com.renwer.utils.StringUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Controller implements ConnectionListener {

    private static final String DEFAULT_NAME = "Anonymous";

    private HashMap<Connection, Tab> connections = new HashMap<>();
    private HashMap<Tab, String[]> userLists = new HashMap<>();

    @FXML
    private TextField messageField;
    @FXML
    private VBox activeUsers;
    @FXML
    private ScrollPane messagePane;
    @FXML
    private TabPane chatsTabPane;

    public Controller() {
    }

    @Override
    public void onConnectionReady(Connection connection) {
        connection.sendString("TYPE:REGISTER USERNAME:" + connection.getUserName());
    }

    @Override
    public void onStringReceived(Connection connection, String string) {
        System.out.println("Received message: " + string);

        if(string.startsWith("TYPE:REGISTER")) {
            String userName = StringUtils.getParameterFromString(string, "USERNAME");
            String serverName = StringUtils.getParameterFromString(string, "SERVERNAME");
            VBox messageBox = new VBox();
            messageBox.setStyle("-fx-background-color: #2c4359;");
            Tab newChat = new Tab();
            newChat.setText(serverName);
            newChat.setClosable(true);
            newChat.setContent(messageBox);
            newChat.setOnClosed((event)->{
                connection.abortConnection();
            });
            newChat.setOnSelectionChanged((event -> {
                updateUserListForTab(newChat);
            }));

            Platform.runLater(()->{
                chatsTabPane.getTabs().add(newChat);
            });

            connection.setUserName(userName);

            connections.put(connection, newChat);

            addMessageToChat(connection, "Connected as " + userName);
        } else if(string.startsWith("TYPE:USER_LIST")) {
            String userNameList = StringUtils.getParameterFromString(string, "USERS");
            String[] userNameListArray = userNameList.split(",");
            changeUserListForConnection(connection, userNameListArray);
            Tab currentTab = connections.get(connection);
            updateUserListForTab(currentTab);
        } else {
            addMessageToChat(connection, string);
        }
    }

    @Override
    public void onAbortConnection(Connection connection) {
        addMessageToChat(connection, "Disconnected");
    }

    @Override
    public void onException(Connection connection, Exception e) {
        addMessageToChat(connection, "Got some problem: " + e.getMessage());
    }

    @FXML
    private void handleEnterButtonPressed(KeyEvent ke) {
        if(ke.getCode() == KeyCode.ENTER){
            onSendButtonClicked();
        }
    }

    @FXML
    private void onSendButtonClicked() {
        if (!messageField.getText().equals("")) {
            String messageText = messageField.getText();
            Tab chosenTab = chatsTabPane.getSelectionModel().getSelectedItem();
            if(chosenTab != null) {
                try {
                    Connection connectionForThisTab = getConnectionByTab(chosenTab);
                    connectionForThisTab.sendString(messageText);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }
            messageField.setText("");
        }
    }

    private void addMessageToChat(Connection connection, String message) {
        if(message != null && message.length() != 0) {
            Label messageLabel = new Label(message);
            messageLabel.setStyle("-fx-background-color: #d7f9c5; -fx-padding: 10px; -fx-background-insets: 5px; -fx-font-size: 15px; -fx-background-radius: 8px");
            Tab currentChat = connections.get(connection);
            if(currentChat.getContent() instanceof VBox){
                VBox currentMessages = (VBox)currentChat.getContent();

                Platform.runLater(() -> {
                    currentMessages.getChildren().add(messageLabel);
                    currentMessages.heightProperty().addListener(observable -> messagePane.setVvalue(1D));
                    messageField.requestFocus();
                });
            }
        }
    }

    private void changeUserListForConnection(Connection connection, String[] userNameList) {
        Tab tabForChange = connections.get(connection);
        userLists.put(tabForChange, userNameList);
    }

    private void updateUserListForTab(Tab tab){
        Platform.runLater(() -> {
                    activeUsers.getChildren().clear();

                    String[] userNameList = userLists.get(tab);
                    for (String s : userNameList) {
                        Label userLabel = new Label(s);
                        userLabel.setStyle("-fx-padding: 5px");
                        activeUsers.getChildren().add(userLabel);
                    }
                }
        );
    }

    @FXML
    private void createNewConnection(){
        try {
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

            if(command == PressedButton.OK) {
                String userName = controller.getUserName();
                String ipAddress = controller.getIpAddress();
                int port = controller.getPort();
                Connection connection = new Connection(this, ipAddress, port, userName);
                connection.init();
            }else if(command == PressedButton.ANONYMOUS) {
                String ipAddress = controller.getIpAddress();
                int port = controller.getPort();
                Connection connection = new Connection(this, ipAddress, port, DEFAULT_NAME);
                connection.init();
            }

        } catch(IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private Connection getConnectionByTab(Tab chosenTab) throws IllegalArgumentException{
        Set<Map.Entry<Connection, Tab>> entrySet = connections.entrySet();
        for(Map.Entry<Connection, Tab> entry : entrySet){
            if(entry.getValue().equals(chosenTab)){
                return entry.getKey();
            }
        }

        throw new IllegalArgumentException("No connection for this Tab");
    }
}
