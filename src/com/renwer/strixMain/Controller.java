package com.renwer.strixMain;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;


public class Controller {


    @FXML
    private TextField messageField;
    @FXML
    private VBox messages;


    public void onSendButtonClicked() {
        if (!messageField.getText().equals("")) {
            Label message = new Label(messageField.getText());
            message.setStyle("-fx-background-color: cyan; -fx-padding: 10px; -fx-background-insets: 5px;");
            message.setOpacity(0.55);
            messageField.setText("");
            messages.getChildren().add(message);
            messageField.requestFocus();
        }
    }

    public void onMessageToServer() {

    }

}
