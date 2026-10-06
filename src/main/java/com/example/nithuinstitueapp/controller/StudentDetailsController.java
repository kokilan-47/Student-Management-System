package com.example.nithuinstitueapp.controller;

import com.example.nithuinstitueapp.model.Student;
import com.example.nithuinstitueapp.service.StudentService;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import javafx.scene.input.MouseEvent;
import java.sql.Date;
import java.time.format.DateTimeFormatter;

public class StudentDetailsController {

    private MenuController menuController;
    @FXML
    private TableColumn<Student, Integer> idCol;
    @FXML
    private TableColumn<Student, String> nameCol;
    @FXML
    private TableColumn<Student, Date> dobCol;
    @FXML
    private TableColumn<Student, String> gradeCol;
    @FXML
    private TableColumn<Student, String> genderCol;
    @FXML
    private TableColumn<Student, String> addressCol;
    @FXML
    private TableColumn<Student, String> contactCol;
    @FXML
    private TableColumn<Student , String > emailCol;

    // Parent Information Columns
    @FXML
    private TableColumn<Student, String> parentNameCol;
    @FXML
    private TableColumn<Student, String> parentRelationCol;
    @FXML
    private TableColumn<Student, String> parentContactCol;

    @FXML
    private TableView<Student> studentTableView;

    @FXML private TextField nameStView;
    @FXML private TextField gradeStView;



    public void initialize(){
        studentTableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        initializeColumns();
        studentTableView.getItems().setAll(StudentService.getAllStudents());
    }

    private void initializeColumns() {
        // Student columns
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        // Date formatting for DOB
        dobCol.setCellValueFactory(new PropertyValueFactory<>("dob"));
        dobCol.setCellFactory(column -> new TableCell<Student, Date>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            @Override
            protected void updateItem(Date date, boolean empty) {
                super.updateItem(date, empty);
                setText(empty || date == null ? "" : date.toLocalDate().format(formatter));
            }
        });

        gradeCol.setCellValueFactory(new PropertyValueFactory<>("grade"));
        genderCol.setCellValueFactory(new PropertyValueFactory<>("gender"));
        addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
        contactCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));


        // Parent columns
        parentNameCol.setCellValueFactory(new PropertyValueFactory<>("parentName"));
        parentRelationCol.setCellValueFactory(new PropertyValueFactory<>("parentRelation"));
        parentContactCol.setCellValueFactory(new PropertyValueFactory<>("parentContact"));
    }
    public void setMenuController(MenuController menuController) {
        this.menuController = menuController;
    }
    @FXML
    private void searchDetails (){
        studentTableView.getItems().setAll(StudentService.searchStudents(nameStView.getText(), gradeStView.getText()));
    }

    @FXML
    private void resetSearch (){
        nameStView.clear();
        gradeStView.clear();
        studentTableView.getItems().setAll(StudentService.getAllStudents());
    }


    @FXML public void handleDoubleClick(MouseEvent e) {
        if(e.getClickCount() == 2){
            Student selectedStudent = studentTableView.getSelectionModel().getSelectedItem();
            if(selectedStudent != null){
                menuController.loadStudentEditView(selectedStudent);
            }
        }
    }

}
