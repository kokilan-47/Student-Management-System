package com.example.nithuinstitueapp.common;

import com.example.nithuinstitueapp.model.Student;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;

public class DBController {
    private static final String URL;
    private static final String USER;
    private static final String PASSWORD;

    static {
        Properties props = loadProperties();
        URL = setting("NITHU_DB_URL", props, "db.url", "jdbc:mysql://localhost:3306/nithuinstitutedb");
        USER = setting("NITHU_DB_USER", props, "db.user", "root");
        PASSWORD = setting("NITHU_DB_PASSWORD", props, "db.password", "");
    }

    private static String authUser;
    private static String retrievedRole = "";

    private static final String STUDENT_SELECT =
            "SELECT S.StudentID, S.FullName, S.DateOfBirth, S.Gender, S.Grade, S.Address, S.PhoneNumber, S.Email, " +
            "P.ParentID, P.FullName AS ParentName, P.Relationship, P.ContactNumber " +
            "FROM student S JOIN parent P ON S.StudentID = P.StudentID";

    // Settings are read from environment variables first, then ./db.properties, then the bundled defaults.
    private static Properties loadProperties() {
        Properties props = new Properties();
        try (InputStream in = DBController.class.getResourceAsStream("/db.properties")) {
            if (in != null) props.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Path local = Path.of("db.properties");
        if (Files.exists(local)) {
            try (InputStream in = Files.newInputStream(local)) {
                props.load(in);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return props;
    }

    private static String setting(String envKey, Properties props, String propKey, String fallback) {
        String env = System.getenv(envKey);
        if (env != null && !env.isBlank()) return env;
        return props.getProperty(propKey, fallback);
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static boolean logInUser(String username, String password) {
        try (Connection conn = getConnection()) {
            String sql = "SELECT Password, Role FROM `user` WHERE Username = ?";
            PreparedStatement prst = conn.prepareStatement(sql);
            prst.setString(1, username);
            ResultSet resultSet = prst.executeQuery();

            if (!resultSet.next()) {
                NotificationController.errorNotification("Error", "User not found!!");
                return false;
            }
            if (PasswordUtil.verify(password, resultSet.getString("Password"))) {
                authUser = username;
                retrievedRole = resultSet.getString("Role");
                return true;
            }
            NotificationController.errorNotification("Error", "Incorrect password");
        } catch (SQLException e) {
            e.printStackTrace();
            NotificationController.errorNotification("Database Error", "Could not connect to the database");
        }
        return false;
    }

    public static void logOut() {
        authUser = null;
        retrievedRole = "";
    }

    public static String getLogInUsername() {
        return authUser;
    }

    public static String getLogInUserRole() {
        return retrievedRole;
    }

    public static boolean isAdmin() {
        return "Admin".equals(retrievedRole);
    }

    public static boolean createUser(String username, String password, String role) {
        String sql = "INSERT INTO `user` (Username, Password, Role) VALUES (?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement prst = conn.prepareStatement(sql)) {
            prst.setString(1, username);
            prst.setString(2, PasswordUtil.hash(password));
            prst.setString(3, role);
            prst.executeUpdate();
            NotificationController.informNotification("Success", "User " + username + " created");
            return true;
        } catch (SQLIntegrityConstraintViolationException e) {
            NotificationController.errorNotification("Error", "Username " + username + " already exists");
        } catch (SQLException e) {
            e.printStackTrace();
            NotificationController.errorNotification("Error", "Failed to create user");
        }
        return false;
    }

    public static boolean insertStudentWithParent(
            String fullName, Date dateOfBirth, String gender, String grade,
            String address, String phoneNumber, String email,
            String parentName, String relationship, String parentPhoneNumber
    ) {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            int studentId = insertStudent(fullName, dateOfBirth, gender, grade, address, phoneNumber, email, conn);
            if (studentId == -1) {
                throw new SQLException("Student insert failed");
            }

            int parentId = insertParent(parentName, relationship, parentPhoneNumber, studentId, conn);
            if (parentId == -1) {
                throw new SQLException("Parent insert failed");
            }

            conn.commit();
            NotificationController.informNotification("Success", "Student and Parent registered successfully.");
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            rollback(conn);
            NotificationController.errorNotification("Error", "Failed to register student/parent.");
            return false;
        } finally {
            close(conn);
        }
    }

    private static int insertStudent(String fullName, Date dateOfBirth, String gender, String grade,
                                     String address, String phoneNumber, String email, Connection conn) throws SQLException {
        String sql = "INSERT INTO student (FullName, DateOfBirth, Gender, Grade, Address, PhoneNumber, Email) VALUES (?, ?, ?, ?, ?, ?, ?)";
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
        String sql = "INSERT INTO parent (FullName, Relationship, ContactNumber, StudentID) VALUES (?, ?, ?, ?)";
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
    ) {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            int studentId_updated = updateStudent(studentId, fullName, dateOfBirth, gender, grade, address, phoneNumber, email, conn);
            if (studentId_updated == -1) {
                throw new SQLException("Student Update failed");
            }

            int parentId_updated = updateParent(parentId, parentName, relationship, parentPhoneNumber, studentId, conn);
            if (parentId_updated == -1) {
                throw new SQLException("Parent Update failed");
            }

            conn.commit();
            NotificationController.informNotification("Success", "Student and Parent Updated successfully.");
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            rollback(conn);
            NotificationController.errorNotification("Error", "Failed to Update student/parent.");
            return false;
        } finally {
            close(conn);
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
        String sql = "UPDATE parent SET FullName = ?, Relationship = ?, ContactNumber = ?, StudentID = ? WHERE ParentID = ?";
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

    // Parent rows are removed by the ON DELETE CASCADE foreign key.
    public static boolean deleteStudent(int studentId) {
        String sql = "DELETE FROM student WHERE StudentID = ?";
        try (Connection conn = getConnection(); PreparedStatement prst = conn.prepareStatement(sql)) {
            prst.setInt(1, studentId);
            if (prst.executeUpdate() > 0) {
                NotificationController.informNotification("Success", "Student ID : " + studentId + " Deleted Successfully");
                return true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        NotificationController.errorNotification("Error", "Failed to delete student ID : " + studentId);
        return false;
    }

    public static ArrayList<Student> LoadStudents() {
        ArrayList<Student> studentList = new ArrayList<>();
        try (Connection conn = getConnection()) {
            PreparedStatement prst = conn.prepareStatement(STUDENT_SELECT + " ORDER BY S.StudentID");
            ResultSet rs = prst.executeQuery();
            while (rs.next()) {
                studentList.add(mapStudent(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            NotificationController.errorNotification("Database Error", "Could not load students");
        }
        return studentList;
    }

    // Partial, case-insensitive name match; an empty grade matches every grade.
    public static ArrayList<Student> searchStudent(String name, String grade) {
        ArrayList<Student> studentList = new ArrayList<>();
        try (Connection conn = getConnection()) {
            String sql = STUDENT_SELECT + " WHERE LOWER(S.FullName) LIKE LOWER(?) AND (? = '' OR S.Grade = ?) ORDER BY S.StudentID";
            PreparedStatement prst = conn.prepareStatement(sql);
            String gradeValue = grade == null ? "" : grade.trim();
            prst.setString(1, "%" + (name == null ? "" : name.trim()) + "%");
            prst.setString(2, gradeValue);
            prst.setString(3, gradeValue);
            ResultSet rs = prst.executeQuery();
            while (rs.next()) {
                studentList.add(mapStudent(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            NotificationController.errorNotification("Database Error", "Could not search students");
        }
        return studentList;
    }

    public static String studentCount() {
        String count = "0";
        try (Connection conn = getConnection()) {
            String sql = "SELECT COUNT(*) as count FROM student";
            PreparedStatement prst = conn.prepareStatement(sql);
            ResultSet rs = prst.executeQuery();
            if (rs.next()) {
                count = rs.getString("count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    private static Student mapStudent(ResultSet rs) throws SQLException {
        Student student = new Student(
                rs.getString("FullName"),
                rs.getDate("DateOfBirth"),
                rs.getString("Gender"),
                rs.getString("Grade"),
                rs.getString("Address"),
                rs.getString("PhoneNumber"),
                rs.getString("Email"),
                rs.getString("ParentName"),
                rs.getString("ContactNumber"),
                rs.getString("Relationship")
        );
        student.setId(rs.getInt("StudentID"));
        student.setParentId(rs.getInt("ParentID"));
        return student;
    }

    private static void rollback(Connection conn) {
        try {
            if (conn != null) conn.rollback();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private static void close(Connection conn) {
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
