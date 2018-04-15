package com.renwer.strixMain;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;


public class Controller {


    @FXML
    private TextField messageField;
    @FXML
    private VBox messages;
    @FXML
    private VBox activeUsers;
    @FXML
    private ScrollPane messagePane;



    public void onSendButtonClicked() {
        if (!messageField.getText().equals("")) {
            Label message = new Label(messageField.getText());
            message.setStyle("-fx-background-color: #d7f9c5; -fx-padding: 10px; -fx-background-insets: 5px; -fx-font-size: 15px; -fx-background-radius: 8px");
            messageField.setText("");
            messages.getChildren().add(message);
        }
        messages.heightProperty().addListener(observable -> messagePane.setVvalue(1D));
        messageField.requestFocus();
    }

    public void onUserLogin(String username) {

    }


}
