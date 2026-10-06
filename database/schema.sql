-- Nithu Institute database schema
-- Run with: mysql -u root -p < database/schema.sql

CREATE DATABASE IF NOT EXISTS nithuinstitutedb
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE nithuinstitutedb;

CREATE TABLE IF NOT EXISTS `user` (
    UserID    INT AUTO_INCREMENT PRIMARY KEY,
    Username  VARCHAR(50)  NOT NULL UNIQUE,
    Password  VARCHAR(255) NOT NULL,          -- PBKDF2 hash, never plain text
    Role      ENUM('Admin', 'Staff') NOT NULL DEFAULT 'Staff',
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS student (
    StudentID   INT AUTO_INCREMENT PRIMARY KEY,
    FullName    VARCHAR(100) NOT NULL,
    DateOfBirth DATE         NOT NULL,
    Gender      VARCHAR(10)  NOT NULL,
    Grade       VARCHAR(5)   NOT NULL,
    Address     VARCHAR(255),
    PhoneNumber VARCHAR(20),
    Email       VARCHAR(100) NOT NULL,
    CreatedAt   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS parent (
    ParentID      INT AUTO_INCREMENT PRIMARY KEY,
    FullName      VARCHAR(100) NOT NULL,
    Relationship  VARCHAR(20)  NOT NULL,
    ContactNumber VARCHAR(20),
    StudentID     INT NOT NULL,
    CONSTRAINT fk_parent_student FOREIGN KEY (StudentID)
        REFERENCES student (StudentID) ON DELETE CASCADE
);

-- Default accounts (change these passwords after first login):
--   admin / admin123  (Admin - full access)
--   staff / staff123  (Staff - view and register only)
INSERT IGNORE INTO `user` (Username, Password, Role) VALUES
    ('admin', 'pbkdf2$210000$6Gzugu8fdcnc+Jk+kgYFTg==$Rj2vBVhGJVntiUa6/fuxNDgkqNbDTiq+IoW7K4pvypA=', 'Admin'),
    ('staff', 'pbkdf2$210000$L5qzX+MN+ZF3wCJoo7bekw==$SG7sJ0L+eqrzyAbiGfhoODY/gktxXo3jsrQ1B7vfIMA=', 'Staff');

-- Sample data
INSERT INTO student (FullName, DateOfBirth, Gender, Grade, Address, PhoneNumber, Email)
SELECT * FROM (SELECT 'Nimal Perera', '2010-04-12', 'Male', '10', '12 Lake Road, Colombo', '0771234567', 'nimal@example.com') AS s
WHERE NOT EXISTS (SELECT 1 FROM student);
INSERT INTO parent (FullName, Relationship, ContactNumber, StudentID)
SELECT 'Sunil Perera', 'Father', '0777654321', StudentID FROM student
WHERE FullName = 'Nimal Perera' AND NOT EXISTS (SELECT 1 FROM parent);
