package com.example.nithuinstitueapp.service;

import com.example.nithuinstitueapp.common.DBController;
import com.example.nithuinstitueapp.model.Student;

import java.util.List;

public class StudentService {

    public static boolean registerStudent(Student student) {
        return DBController.insertStudentWithParent(
                student.getName(), student.getDob(), student.getGender(), student.getGrade(),
                student.getAddress(), student.getPhone(), student.getEmail(),
                student.getParentName(), student.getParentRelation(), student.getParentContact()
        );
    }

    public static boolean updateStudent(Student student) {
        return DBController.updateStudentWithParent(
                student.getId(), student.getName(), student.getDob(), student.getGender(), student.getGrade(),
                student.getAddress(), student.getPhone(), student.getEmail(),
                student.getParentId(), student.getParentName(), student.getParentRelation(), student.getParentContact()
        );
    }

    public static boolean deleteStudent(int studentId) {
        return DBController.deleteStudent(studentId);
    }

    public static List<Student> getAllStudents() {
        return DBController.LoadStudents();
    }

    public static List<Student> searchStudents(String name, String grade) {
        return DBController.searchStudent(name, grade);
    }
}
