package com.example.nithuinstitueapp.controller;

import com.example.nithuinstitueapp.common.NotificationController;
import com.example.nithuinstitueapp.model.Student;
import com.example.nithuinstitueapp.service.StudentService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.Date;

public class NewStudentController {
    // Student Fields
    @FXML private TextField nameStudent;
    @FXML private TextField addressStudent;
    @FXML private ComboBox<String> gradeStudent;
    @FXML private RadioButton maleRadio;
    @FXML private RadioButton femaleRadio;
    @FXML private RadioButton otherRadio;
    @FXML private DatePicker dobStudent;
    @FXML private TextField contactStudent;
    @FXML private TextField emailStudent;

    // Parent fields
    @FXML private TextField nameParent;
    @FXML private TextField contactParent;
    @FXML private ComboBox<String> relationParent;

    @FXML private Button addButton;
    @FXML private Button clearButton;
    @FXML private Button enrollSubjectButton;
    @FXML private Button viewStudentButton;

    private ToggleGroup genderGroup;

    @FXML
    public void initialize() {
        initializeComboBoxes();



    }

    private void initializeComboBoxes() {
        genderGroup = new ToggleGroup();
        maleRadio.setToggleGroup(genderGroup);
        femaleRadio.setToggleGroup(genderGroup);
        otherRadio.setToggleGroup(genderGroup);
        otherRadio.setSelected(true);

        gradeStudent.getItems().addAll("6", "7", "8","9","10","11","12","13");

        relationParent.getItems().addAll(
                "Father", "Mother", "Guardian", "Other"
        );
    }
    @FXML private void insertStudent() {
        if (validateInputs()) {
            String selectedGender = getSelectedGender();
            Student student = new Student(
                    nameStudent.getText(),
                    Date.valueOf(dobStudent.getValue()),
                    selectedGender,
                    gradeStudent.getValue(),
                    addressStudent.getText(),
                    contactStudent.getText(),
                    emailStudent.getText(),
                    nameParent.getText(),
                    contactParent.getText(),
                    relationParent.getValue()
            );

            StudentService.registerStudent(student);
            }

    }

        private boolean validateInputs () {
            if (nameStudent.getText().trim().isEmpty()) {
                NotificationController.errorNotification("Check Input Data", "Student name is required");
                return false;
            }

            if (dobStudent.getValue() == null) {
                NotificationController.errorNotification("Check Input Data", "Date of birth is required");
                return false;
            }

            if (genderGroup.getSelectedToggle() == null) {
                NotificationController.errorNotification("Check Input Data", "Please select gender");
                return false;
            }

            if (emailStudent.getText().trim().isEmpty()) {
                NotificationController.errorNotification("Check Input Data", "Email is required");
                return false;
            }

            if (nameParent.getText().trim().isEmpty()) {
                NotificationController.errorNotification("Check Input Data", "Parent name is required");return false;
            }

            if (relationParent.getValue() == null) {
                NotificationController.errorNotification("Check Input Data", "Please select relationship");return false;
            }

            return true;
        }

    private String getSelectedGender() {
        RadioButton selected = (RadioButton) genderGroup.getSelectedToggle();
        return selected != null ? selected.getText() : "Other"; // Default to "Other"
    }


}