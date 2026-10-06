package com.example.nithuinstitueapp.common;

import com.example.nithuinstitueapp.model.Student;

import java.sql.*;
import java.util.ArrayList;

public class DBController {
    private static final String URL = "jdbc:mysql://localhost:3306/nithuinstitutedb";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private static String authUser;
    private static String retrievedRole = "";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static boolean logInUser(String username, String password) {
        try (Connection conn = getConnection()) {
            String sql = "SELECT password, role FROM user WHERE username = ?";
            PreparedStatement prst = conn.prepareStatement(sql);
            prst.setString(1, username);
            ResultSet resultSet = prst.executeQuery();

            if (!resultSet.isBeforeFirst()) {
                NotificationController.errorNotification("Error", "User not found!!");
                return false;
            } else {
                while (resultSet.next()) {
                    String retrievedPassword = resultSet.getString("Password");
                    retrievedRole = resultSet.getString("Role");
                    if (retrievedPassword.equals(password)) {
                        authUser = username;
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getLogInUsername() {
        return authUser;
    }

    public static String getLogInUserRole() {
        return retrievedRole;
    }

    public static boolean insertStudentWithParent(
            String fullName, Date dateOfBirth, String gender, String grade,
            String address, String phoneNumber, String email,
            String parentName, String relationship, String parentPhoneNumber
    ) {
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            int studentId = insertStudent(fullName, dateOfBirth, gender, grade, address, phoneNumber, email, conn);
            if (studentId == -1) {
                NotificationController.errorNotification("Error", "Student insert failed");
                throw new SQLException("Student insert failed");
            }

            int parentId = insertParent(parentName, relationship, parentPhoneNumber, studentId, conn);
            if (parentId == -1) {
                NotificationController.errorNotification("Error", "Parent insert failed");
                throw new SQLException("Parent insert failed");
            }

            conn.commit();
            NotificationController.informNotification("Success", "Student and Parent registered successfully.");
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            try (Connection conn = getConnection()) {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            NotificationController.errorNotification("Error", "Failed to register student/parent.");
            return false;
        }
    }

    private static int insertStudent(String fullName, Date dateOfBirth, String gender, String grade,
                                     String address, String phoneNumber, String email, Connection conn) throws SQLException {
        String sql = "INSERT INTO Student (FullName, DateOfBirth, Gender, Grade, Address, PhoneNumber, Email) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement prst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            prst.setString(1, fullName);
            prst.setDate(2, dateOfBirth);
            prst.setString(3, gender);
            prst.setString(4, grade);
            prst.setString(5, address);
            prst.setString(6, phoneNumber);
            prst.setString(7, email);

            int affectedRows = prst.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = prst.getGeneratedKeys()) {
                    if (rs.next()) return rs.getInt(1);
                }
            }
        }
        return -1;
    }

    private static int insertParent(String parentName, String relationship, String contactNumber,
                                    int studentId, Connection conn) throws SQLException {
        String sql = "INSERT INTO Parent (FullName, Relationship, ContactNumber, StudentID) VALUES (?, ?, ?, ?)";
        try (PreparedStatement prst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            prst.setString(1, parentName);
            prst.setString(2, relationship);
            prst.setString(3, contactNumber);
            prst.setInt(4, studentId);

            int affectedRows = prst.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = prst.getGeneratedKeys()) {
                    if (rs.next()) return rs.getInt(1);
                }
            }
        }
        return -1;
    }

    public static boolean updateStudentWithParent(
            int studentId, String fullName, Date dateOfBirth, String gender, String grade,
            String address, String phoneNumber, String email,
            int parentId, String parentName, String relationship, String parentPhoneNumber
    ) {Connection conn = null;
        try {conn = getConnection();
            conn.setAutoCommit(false);
            int studentId_updated = updateStudent(studentId, fullName, dateOfBirth, gender, grade, address, phoneNumber, email, conn);
            if (studentId_updated == -1) {
                NotificationController.errorNotification("Error", "Student Update failed");
                throw new SQLException("Student Update failed");
            }

            int parentId_updated = updateParent(parentId, parentName, relationship, parentPhoneNumber, studentId, conn);
            if (parentId_updated == -1) {
                NotificationController.errorNotification("Error", "Parent Update failed");
                throw new SQLException("Parent Update failed");
            }

            conn.commit();
            NotificationController.informNotification("Success", "Student and Parent Updated successfully.");
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            NotificationController.errorNotification("Error", "Failed to Update student/parent.");
            return false;
        }finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private static int updateStudent(int studentId, String fullName, Date dateOfBirth, String gender, String grade,
                                     String address, String phoneNumber, String email, Connection conn) throws SQLException {
        String sql = "UPDATE student SET FullName = ?, DateOfBirth = ?, Gender = ?, Grade = ?, Address = ?, PhoneNumber = ?, Email = ? WHERE StudentID = ?";
        try (PreparedStatement prst = conn.prepareStatement(sql)) {
            prst.setString(1, fullName);
            prst.setDate(2, dateOfBirth);
            prst.setString(3, gender);
            prst.setString(4, grade);
            prst.setString(5, address);
            prst.setString(6, phoneNumber);
            prst.setString(7, email);
            prst.setInt(8, studentId);

            int affectedRows = prst.executeUpdate();
            if (affectedRows > 0) {
                return studentId;
            }
        }
        return -1;
    }

    private static int updateParent(int parentId, String parentName, String relationship, String contactNumber,
                                    int studentId, Connection conn) throws SQLException {
        String sql = "UPDATE Parent SET FullName = ?, Relationship = ?, ContactNumber = ?, StudentID = ? WHERE ParentID = ?";
        try (PreparedStatement prst = conn.prepareStatement(sql)) {
            prst.setString(1, parentName);
            prst.setString(2, relationship);
            prst.setString(3, contactNumber);
            prst.setInt(4, studentId);
            prst.setInt(5, parentId);

            int affectedRows = prst.executeUpdate();
            if (affectedRows > 0) {
                return parentId;
            }
        }
        return -1;
    }

    public static boolean deleteStudent(int studentId){
        Connection conn = null;
        try{
            conn = getConnection();
            conn.setAutoCommit(false);
            String sql = "Delete from Student where StudentID  = ?";
            PreparedStatement prst = conn.prepareStatement(sql);
            prst.setInt(1, studentId);
            int affecteRow = prst.executeUpdate();
            if (affecteRow> 0){
                NotificationController.informNotification("Success", "Student ID : " +studentId+" Deleted Successfully");
                return true;
            }
        }catch (SQLException ex){
            ex.printStackTrace();
        }finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return false;
    }
    public static ArrayList<Student> LoadStudents() {
        ArrayList<Student> studentList = new ArrayList<>();
        try (Connection conn = getConnection()) {
            String sql = "SELECT * FROM Student S JOIN Parent P ON S.StudentID = P.StudentID";
            PreparedStatement prst = conn.prepareStatement(sql);
            ResultSet rs = prst.executeQuery();
            while (rs.next()) {
                Student student = new Student(
                        rs.getString("FullName"), // Fixed typo: was "FullNAme"
                        rs.getDate("DateOfBirth"),
                        rs.getString("Gender"),
                        rs.getString("Grade"),
                        rs.getString("Address"),
                        rs.getString("PhoneNumber"),
                        rs.getString("Email"),
                        rs.getString(10), // Parent FullName - using column index to avoid ambiguity
                        rs.getString("ContactNumber"),
                        rs.getString("Relationship")
                );
                student.setId(rs.getInt("StudentID"));
                student.setParentId(rs.getInt("ParentID"));
                studentList.add(student);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return studentList;
    }

    public static Student searchStudent(String name, String grade) {
        Student student = null;
        try (Connection conn = getConnection()) {
            String sql = "SELECT * FROM Student S JOIN Parent P ON S.StudentID = P.StudentID WHERE S.FullName = ? AND S.Grade = ?";
            PreparedStatement prst = conn.prepareStatement(sql);
            prst.setString(1, name);
            prst.setString(2, grade);
            ResultSet rs = prst.executeQuery();

            if (rs.next()) { // Use if instead of while since we expect only one result
                student = new Student(
                        rs.getString("FullName"), // Fixed typo: was "FullNAme"
                        rs.getDate("DateOfBirth"),
                        rs.getString("Gender"),
                        rs.getString("Grade"),
                        rs.getString("Address"),
                        rs.getString("PhoneNumber"),
                        rs.getString("Email"),
                        rs.getString(10), // Parent FullName - using column index to avoid ambiguity
                        rs.getString("ContactNumber"),
                        rs.getString("Relationship")
                );
                student.setId(rs.getInt("StudentID"));
                student.setParentId(rs.getInt("ParentID"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return student;
    }

    public static String studentCount() {
        String count = "0";
        try (Connection conn = getConnection()) {
            String sql = "SELECT COUNT(*) as count FROM Student";
            PreparedStatement prst = conn.prepareStatement(sql);
            ResultSet rs = prst.executeQuery();
            if (rs.next()) { // Use if instead of while
                count = rs.getString("count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }
}