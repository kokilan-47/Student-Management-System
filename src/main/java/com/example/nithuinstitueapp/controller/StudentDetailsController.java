package com.example.nithuinstitueapp.controller;

import com.example.nithuinstitueapp.common.DBController;
import com.example.nithuinstitueapp.model.Student;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import javafx.scene.input.MouseEvent;
import java.io.IOException;
import java.time.LocalDate;

public class StudentDetailsController {

    private MenuController menuController;
    @FXML
    private TableColumn<Student, Integer> idCol;
    @FXML
    private TableColumn<Student, String> nameCol;
    @FXML
    private TableColumn<Student, LocalDate> dobCol;
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
    private ScrollPane scrollPane;

    @FXML
    private TableView<Student> studentTableView;

    @FXML private TextField nameStView;
    @FXML private TextField gradeStView;



    public void initialize(){
        scrollPane.setFitToHeight(false);
        scrollPane.setFitToWidth(false);
        studentTableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        studentTableView.minWidthProperty().bind(
                Bindings.createDoubleBinding(() ->
                                studentTableView.getColumns().stream()
                                        .mapToDouble(TableColumn::getWidth)
                                        .sum(),
                        studentTableView.getColumns()
                )
        );
        initializeColumns();
    }
    @FXML
    public void initializeColumns() {
        // Student columns
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        // Date formatting for DOB
        dobCol.setCellValueFactory(new PropertyValueFactory<>("dob"));
//        dobCol.setCellFactory(column -> new TableCell<Student, Date>() {
//            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//
//            @Override
//            protected void updateItem(Date date, boolean empty) {
//                super.updateItem(date, empty);
//                setText(empty || date == null ? "" : date.toLocalDate().format(formatter));
//            }
//        });

        gradeCol.setCellValueFactory(new PropertyValueFactory<>("grade"));
        genderCol.setCellValueFactory(new PropertyValueFactory<>("gender"));
        addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
        contactCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));


        // Parent columns
        parentNameCol.setCellValueFactory(new PropertyValueFactory<>("parentName"));
        parentRelationCol.setCellValueFactory(new PropertyValueFactory<>("parentRelation"));
        parentContactCol.setCellValueFactory(new PropertyValueFactory<>("parentContact"));
        studentTableView.getItems().setAll(DBController.LoadStudents());
    }
    public void setMenuController(MenuController menuController) {
        this.menuController = menuController;
    }
    @FXML
    private void searchDetails (){
        // Student columns
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        // Date formatting for DOB
        dobCol.setCellValueFactory(new PropertyValueFactory<>("dob"));
//        dobCol.setCellFactory(column -> new TableCell<Student, Date>() {
//            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//
//            @Override
//            protected void updateItem(Date date, boolean empty) {
//                super.updateItem(date, empty);
//                setText(empty || date == null ? "" : date.toLocalDate().format(formatter));
//            }
//        });

        gradeCol.setCellValueFactory(new PropertyValueFactory<>("grade"));
        genderCol.setCellValueFactory(new PropertyValueFactory<>("gender"));
        addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
        contactCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));


        // Parent columns
        parentNameCol.setCellValueFactory(new PropertyValueFactory<>("parentName"));
        parentRelationCol.setCellValueFactory(new PropertyValueFactory<>("parentRelation"));
        parentContactCol.setCellValueFactory(new PropertyValueFactory<>("parentContact"));
        studentTableView.getItems().setAll(DBController.searchStudent(nameStView.getText(),gradeStView.getText()));
    }


    @FXML public void handleDoubleClick(MouseEvent e) throws IOException {
        if(e.getClickCount() == 2){
            Student selectedStudent = studentTableView.getSelectionModel().getSelectedItem();
            if(selectedStudent != null){
                menuController.loadStudentEditView(selectedStudent);
            }
        }
    }

}