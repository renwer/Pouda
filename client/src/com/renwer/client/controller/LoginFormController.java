package com.renwer.client.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginFormController {

    @FXML
    private Button okButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Button anonymousButton;

    @FXML
    private TextField userNameTextField;

    private Stage dialogStage;
    private String userName;
    private PressedButton command;

    public PressedButton getCommand(){
        return command;
    }

    public String getUserName(){
        return userName;
    }

    public void setDialogStage(Stage dialogStage){
        this.dialogStage = dialogStage;
    }

    @FXML
    private void handleOk(){
        String userName = userNameTextField.getText();
        if(userName != null && userName.length() != 0){
            this.userName = userName;
            command = PressedButton.OK;
            dialogStage.close();
        }
    }

    @FXML
    private void handleCancel(){
        command = PressedButton.CANCEL;
    }

    @FXML
    private void handleAnonymous(){
        command = PressedButton.ANONYMOUS;
        dialogStage.close();
    }
}
