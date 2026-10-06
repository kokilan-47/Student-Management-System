package com.example.nithuinstitueapp.controller;

import com.example.nithuinstitueapp.common.DBController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class ProfileController {
    private MenuController menuController;

    @FXML
    private Label usernameLabel;
    @FXML
    private Label roleLabel;
    @FXML private Button newUserBtn;

    public void initialize() {
        usernameLabel.setText(DBController.getLogInUsername());
        roleLabel.setText(DBController.getLogInUserRole());
        if (!DBController.isAdmin()) {
            newUserBtn.setDisable(true);
        }
    }

    public void setMenuController(MenuController menuController) {
        this.menuController = menuController;
    }

    @FXML
    private void openNewUser() {
        menuController.navigateNewUser();
    }
}
