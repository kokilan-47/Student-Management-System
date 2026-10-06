package com.example.nithuinstitueapp.controller;

import com.example.nithuinstitueapp.common.DBController;
import com.example.nithuinstitueapp.common.NotificationController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class NewUserController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private ComboBox<String> roleCombo;
    @FXML private Button createBtn;

    public void initialize() {
        roleCombo.getItems().addAll("Admin", "Staff");
        roleCombo.setValue("Staff");
        // Only admins may create accounts
        createBtn.setDisable(!DBController.isAdmin());
    }

    @FXML
    private void createUser() {
        if (validateInputs()) {
            if (DBController.createUser(usernameField.getText().trim(), passwordField.getText(), roleCombo.getValue())) {
                clearForm();
            }
        }
    }

    @FXML
    private void clearForm() {
        usernameField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
        roleCombo.setValue("Staff");
    }

    private boolean validateInputs() {
        if (usernameField.getText().trim().isEmpty()) {
            NotificationController.errorNotification("Check Input Data", "Username is required");
            return false;
        }
        if (passwordField.getText().length() < 6) {
            NotificationController.errorNotification("Check Input Data", "Password must be at least 6 characters");
            return false;
        }
        if (!passwordField.getText().equals(confirmPasswordField.getText())) {
            NotificationController.errorNotification("Check Input Data", "Passwords do not match");
            return false;
        }
        return true;
    }
}
