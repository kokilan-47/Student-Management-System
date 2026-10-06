package com.example.nithuinstitueapp.controller;

import com.example.nithuinstitueapp.common.DBController;
import com.example.nithuinstitueapp.common.NotificationController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import java.util.Optional;



public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;


    @FXML
    private void handleLogin() {

        String username = usernameField.getText();
        String password = passwordField.getText();
        if (username.isEmpty() || password.isEmpty()) {
            NotificationController.errorNotification("Check", "Enter Both username and password");
            NotificationController.errorNotification("Check", "Enter Both username and password");
        }
        else if (authenticate(username, password)){
            navigateMenu();
        }
        else {
            NotificationController.errorNotification("Unknown", "Login attempted for: " + username);
        }
    }

    @FXML
    private void handleExit(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit Confirmation");
        alert.setHeaderText("Are you sure you want to exit?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Platform.exit();
        }
    }
    private void navigateMenu(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/nithuinstitueapp/view/menu.fxml"));
            Parent root = loader.load();
            MenuController menuController = loader.getController();

            // after login successfull need to load username
            menuController.setUsername(DBController.getLogInUsername());
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
            stage.setTitle("Home");

            NotificationController.informNotification("Success", "Welcome " + DBController.getLogInUsername());

        } catch (Exception e) {
            NotificationController.errorNotification("Error","Check the Credentials");
            e.printStackTrace();
        }
    }
    private boolean authenticate(String username, String password){
        return DBController.logInUser(username, password);
    }
}