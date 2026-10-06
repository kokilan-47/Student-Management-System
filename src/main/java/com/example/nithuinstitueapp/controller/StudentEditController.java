package com.example.nithuinstitueapp.controller;

import com.example.nithuinstitueapp.common.DBController;
import com.example.nithuinstitueapp.common.NotificationController;
import com.example.nithuinstitueapp.model.Student;
import com.example.nithuinstitueapp.service.StudentService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.Date;
import java.util.Optional;

public class StudentEditController {
    private MenuController menuController;
    private int studentID;
    private int parentID;
    private  Student student;
    private ToggleGroup genderGroup;
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

    @FXML private Button updateBtn;
    @FXML private Button deleteBtn;

    @FXML private Label selectdeStudentLabel;
    public void setStudent(Student student){
            this.student = student;
            setDetails();
            studentID = student.getId();
            parentID = student.getParentId();

    }

    public void setMenuController(MenuController menuController){
        this.menuController = menuController;

    }
    public void initialize(){
        initializeComboBoxes();
        checkUser();
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
    private void setDetails(){
        selectdeStudentLabel.setText(student.getName() + " Details");
        nameStudent.setText(student.getName());
        addressStudent.setText(student.getAddress());
        gradeStudent.setValue(student.getGrade());
        String gender = student.getGender();
        if (gender != null) {
            switch (gender.toLowerCase()) {
                case "male":
                    maleRadio.setSelected(true);
                    break;
                case "female":
                    femaleRadio.setSelected(true);
                    break;
                case "other":
                    otherRadio.setSelected(true);
                    break;
                default:
                    otherRadio.setSelected(true);
                    break;
            }
        }else{
            otherRadio.setSelected(true);
        }
        dobStudent.setValue(student.getDob().toLocalDate());
        contactStudent.setText(student.getPhone());
        emailStudent.setText(student.getEmail());

        nameParent.setText(student.getParentName());
        relationParent.setValue(student.getParentRelation());
        contactParent.setText(student.getParentContact());
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

        if (gradeStudent.getValue() == null) {
            NotificationController.errorNotification("Check Input Data", "Please select grade");
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
    @FXML private void updateStudent() {
        if (validateInputs() ) {
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
            student.setId(studentID);
            student.setParentId(parentID);

            if(StudentService.updateStudent(student)){
                menuController.navigateStudentDetails();
            }
        }

    }
    @FXML
    private void deleteStudent(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Confirmation");
        alert.setHeaderText("Delete " + student.getName() + " and their parent details?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK && StudentService.deleteStudent(studentID)) {
            menuController.navigateStudentDetails();
        }
    }
    @FXML
    private void goBack(){
        menuController.navigateStudentDetails();
    }
    private String getSelectedGender() {
        RadioButton selected = (RadioButton) genderGroup.getSelectedToggle();
        return selected != null ? selected.getText() : "Other"; // Default to "Other"
    }
    private boolean checkUser(){
        if (!DBController.isAdmin()){
            updateBtn.setDisable(true);
            deleteBtn.setDisable(true);
            return true;
        }
        return false;
    }

}