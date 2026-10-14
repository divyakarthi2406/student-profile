-- ============================================================
-- StudentProfilePortal Database Setup Script
-- Database: student_portal
-- Compatible with MySQL 8.0+
-- ============================================================

-- 1. Create Database if not exists
CREATE DATABASE IF NOT EXISTS student_portal;
USE student_portal;

-- 2. Drop existing tables if needed (in reverse order of foreign keys)
DROP TABLE IF EXISTS students;
DROP TABLE IF EXISTS users;

-- 3. Create Users Table for Authentication
-- Note: Passwords are stored as SHA-256 hex hashes (64 characters)
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) DEFAULT 'admin',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 4. Create Students Table
CREATE TABLE students (
    id INT AUTO_INCREMENT PRIMARY KEY,
    roll_no VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    department VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    photo_path VARCHAR(255) DEFAULT NULL,
    document_path VARCHAR(255) DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 5. Insert Sample Users
-- Passwords:
-- 'admin'    -> SHA-256 hash of 'admin123'
-- 'faculty'  -> SHA-256 hash of 'faculty123'
INSERT INTO users (username, password, role) VALUES 
('admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'admin'),
('faculty', '27041f5856c7387a997252694afb048d1aa939228ffcdbd6285b979b8da20e7a', 'faculty');

-- 6. Insert Sample Student Records
INSERT INTO students (roll_no, name, department, email, photo_path, document_path) VALUES
('22CS101', 'Alice Johnson', 'Computer Science and Engineering', 'alice.johnson@example.edu', 'alice_photo.png', 'alice_id_card.pdf'),
('22IT102', 'Bob Smith', 'Information Technology', 'bob.smith@example.edu', 'bob_photo.png', 'bob_resume.pdf'),
('22EE103', 'Charlie Brown', 'Electrical & Electronics Engineering', 'charlie.brown@example.edu', NULL, NULL);

-- 7. Verification Query
SELECT * FROM users;
SELECT * FROM students;
