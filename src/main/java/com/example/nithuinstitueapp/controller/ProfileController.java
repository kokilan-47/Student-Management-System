package com.example.nithuinstitueapp.controller;

import com.example.nithuinstitueapp.common.DBController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class ProfileController {
    @FXML
    private Label usernameLabel;
    @FXML
    private Label roleLabel;
    @FXML private Button manageUserBtn;
    @FXML private Button newUserBtn;

    public void initialize() {
    usernameLabel.setText(DBController.getLogInUsername());
    roleLabel.setText(DBController.getLogInUserRole());
    if ((DBController.getLogInUserRole().equals("Staff"))){
        manageUserBtn.setDisable(true);
        newUserBtn.setDisable(true);
    }
    }

}