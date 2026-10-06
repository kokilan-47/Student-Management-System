package com.example.nithuinstitueapp.controller;

import com.example.nithuinstitueapp.common.Common;
import com.example.nithuinstitueapp.common.DBController;
import com.example.nithuinstitueapp.model.Student;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class MenuController {
    @FXML
    private AnchorPane loadPane;

    public AnchorPane getLoadPane(){
        return loadPane;
    }

    public void setUsername(String username){
        usernameBTN.setText("Welcome "+ username + " !" );

    }
    @FXML
    public void initialize() {
        this.navigateDashboard();
    }

    @FXML
    private Button usernameBTN;


    @FXML
    private void navigateNewStudent() {
        String DEFAULT_FXML = "/com/example/nithuinstitueapp/view/newStudent.fxml";
        Common.loadFXML(DEFAULT_FXML ,loadPane);
    }
    @FXML
    public void navigateDashboard() {
        String DEFAULT_FXML = "/com/example/nithuinstitueapp/view/dashboard.fxml";
        Common.loadFXML(DEFAULT_FXML ,loadPane);
    }

    //profile controller
    @FXML
    public void navigateNewUser() {
        String DEFAULT_FXML = "/com/example/nithuinstitueapp/view/newUser.fxml";
        Common.loadFXML(DEFAULT_FXML ,loadPane);
    }
    @FXML
    public void navigateProfile() {
        String DEFAULT_FXML = "/com/example/nithuinstitueapp/view/profile.fxml";

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(DEFAULT_FXML));
            Parent content = loader.load();

            ProfileController controller = loader.getController();
            controller.setMenuController(this);

            setContent(content);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void navigateStudentDetails(){
        String DEFAULT_FXML = "/com/example/nithuinstitueapp/view/studentDetails.fxml";

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(DEFAULT_FXML));
            Parent content = loader.load();

            // Get the controller and pass this MenuController reference
            StudentDetailsController controller = loader.getController();
            controller.setMenuController(this);

            setContent(content);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadStudentEditView(Student student) {
        String DEFAULT_FXML = "/com/example/nithuinstitueapp/view/studentEdit.fxml";

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(DEFAULT_FXML));
            Parent content = loader.load();

            StudentEditController controller = loader.getController();
            controller.setStudent(student);
            controller.setMenuController(this);

            setContent(content);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setContent(Parent content) {
        loadPane.getChildren().setAll(content);
        AnchorPane.setTopAnchor(content, 0.0);
        AnchorPane.setRightAnchor(content, 0.0);
        AnchorPane.setBottomAnchor(content, 0.0);
        AnchorPane.setLeftAnchor(content, 0.0);
    }

    @FXML
    private void handleLogout() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout Confirmation");
        alert.setHeaderText("Are you sure you want to log out?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                DBController.logOut();
                Parent root = FXMLLoader.load(getClass().getResource("/com/example/nithuinstitueapp/view/login.fxml"));
                Stage stage = (Stage) loadPane.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setResizable(false);
                stage.centerOnScreen();
                stage.setTitle("Nithu Institute - Login");
            } catch (IOException e) {
                e.printStackTrace();
            }
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
}
