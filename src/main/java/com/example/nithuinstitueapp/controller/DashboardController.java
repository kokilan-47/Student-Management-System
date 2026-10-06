package com.example.nithuinstitueapp.controller;

import com.example.nithuinstitueapp.common.DBController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class DashboardController {

    @FXML private Label studentCountLabel;

    public void initialize(){
        studentCountLabel.setText(DBController.studentCount());
    }

}