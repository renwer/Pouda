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

    @FXML
    private TextField ipTextField;
    @FXML
    private TextField portTextField;

    private static final String DEFAULT_IP_ADDRESS = "127.0.0.1";
    private static final int DEFAULT_PORT = 8189;

    private Stage dialogStage;
    private String userName;
    private PressedButton command;
    private String ipAddress;
    private int port;

    @FXML
    private void initialize(){
        ipTextField.setText(DEFAULT_IP_ADDRESS);
        portTextField.setText(String.valueOf(DEFAULT_PORT));
    }

    public PressedButton getCommand(){
        return command;
    }

    public String getUserName(){
        return userName;
    }

    public String getIpAddress(){
        return ipAddress;
    }

    public int getPort(){
        return port;
    }

    public void setDialogStage(Stage dialogStage){
        this.dialogStage = dialogStage;
    }

    @FXML
    private void handleOk(){
        String userName = userNameTextField.getText();
        String ipAddress = ipTextField.getText();
        String port = portTextField.getText();

        if(userName != null && userName.length() != 0 &&
                ipAddress != null && ipAddress.length() != 0 &&
                port != null && port.length() != 0){

            this.userName = userName;
            this.ipAddress = ipAddress;
            this.port = Integer.parseInt(port);

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
        String ipAddress = ipTextField.getText();
        String port = portTextField.getText();

        if(ipAddress != null && ipAddress.length() != 0 &&
                port != null && port.length() != 0) {

            this.ipAddress = ipAddress;
            this.port = Integer.parseInt(port);

            command = PressedButton.ANONYMOUS;
            dialogStage.close();
        }
    }
}
