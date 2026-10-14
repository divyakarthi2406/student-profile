# StudentProfilePortal — College Web Technologies Project

A complete, beginner-friendly Java Servlet and JSP web application designed for college Web Technologies laboratory exams and semester assignments.

---

## 🛠️ Technology Stack
* **Language:** Java 17 / Java 21
* **Web Server:** Apache Tomcat 10.1 (Jakarta EE 10)
* **Build Tool:** Apache Maven
* **Backend:** Jakarta Servlets (`jakarta.servlet.*`) & JDBC
* **Frontend:** JavaServer Pages (JSP), JSTL 3.0, HTML5, CSS3
* **Database:** MySQL 8.0+
* **Driver:** MySQL Connector/J 8.3.0

---

## 📂 Project Directory Structure

```text
StudentProfilePortal/
│
├── pom.xml                               # Maven project dependencies & plugins
├── database.sql                          # MySQL database creation & sample data script
├── README.md                             # Step-by-step setup documentation
├── mvnw.cmd / mvnw                       # Maven Wrapper scripts
│
├── uploads/                              # Sample images and PDF documents
│   ├── alice_photo.png
│   ├── alice_id_card.pdf
│   ├── bob_photo.png
│   └── bob_resume.pdf
│
└── src/
    └── main/
        ├── java/
        │   └── com/
        │       └── portal/
        │           ├── model/
        │           │   ├── Student.java       # Student entity bean
        │           │   └── User.java          # User credentials bean
        │           ├── util/
        │           │   ├── DBConnection.java  # JDBC connection manager
        │           │   └── PasswordUtil.java  # SHA-256 password hashing utility
        │           └── servlet/
        │               ├── LoginServlet.java       # Authenticates users & creates HTTP session
        │               ├── LogoutServlet.java      # Destroys session on logout
        │               ├── StudentListServlet.java # Retrieves students via JDBC
        │               └── FileServlet.java        # Secure file streamer (photo & PDF)
        ├── resources/
        │   └── db.properties             # Database connection & uploads configuration
        └── webapp/
            ├── WEB-INF/
            │   └── web.xml               # Web application deployment descriptor
            ├── css/
            │   └── style.css             # Responsive modern CSS design
            ├── index.jsp                 # Entrypoint routing to login or dashboard
            ├── login.jsp                 # Login page with validation
            └── students.jsp              # Student profile cards dashboard
```

---

## 🗄️ Step 1: MySQL Database Configuration

### 1. Open MySQL Command Line or MySQL Workbench
Open your terminal / command prompt or MySQL Workbench and log in as `root`:
```bash
mysql -u root -p
```
*(Enter your MySQL root password when prompted)*

### 2. Execute the Database Script
Run the provided `database.sql` script:
```sql
SOURCE C:/Users/Admin/Downloads/exp-3/database.sql;
```
Or simply copy and execute these SQL commands:

```sql
CREATE DATABASE IF NOT EXISTS student_portal;
USE student_portal;

DROP TABLE IF EXISTS students;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) DEFAULT 'admin',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

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

-- Passwords are encrypted with SHA-256:
-- 'admin123'   -> 240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9
-- 'faculty123' -> 27041f5856c7387a997252694afb048d1aa939228ffcdbd6285b979b8da20e7a
INSERT INTO users (username, password, role) VALUES 
('admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'admin'),
('faculty', '27041f5856c7387a997252694afb048d1aa939228ffcdbd6285b979b8da20e7a', 'faculty');

-- Sample Students (at least 2 with files, and 1 without files to test conditional messages)
INSERT INTO students (roll_no, name, department, email, photo_path, document_path) VALUES
('22CS101', 'Alice Johnson', 'Computer Science and Engineering', 'alice.johnson@example.edu', 'alice_photo.png', 'alice_id_card.pdf'),
('22IT102', 'Bob Smith', 'Information Technology', 'bob.smith@example.edu', 'bob_photo.png', 'bob_resume.pdf'),
('22EE103', 'Charlie Brown', 'Electrical & Electronics Engineering', 'charlie.brown@example.edu', NULL, NULL);
```

---

## ⚙️ Step 2: Configure Database Credentials in the Project

Open `src/main/resources/db.properties` and update your MySQL username, password, and port if needed:

```properties
db.driver=com.mysql.cj.jdbc.Driver

# Default MySQL port is 3306 (or 3307 if configured differently):
db.url=jdbc:mysql://localhost:3306/student_portal?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC

# Replace with your MySQL username & password:
db.user=root
db.password=YOUR_MYSQL_PASSWORD

# Private uploads directory on disk:
upload.dir=C:/student_portal_uploads
```

---

## 📁 Step 3: Set Up the Private Uploads Directory

1. Create a folder named `C:\student_portal_uploads` on your machine.
2. The project has already generated sample files in both the project's `uploads/` folder and `C:\student_portal_uploads\`:
   - `alice_photo.png`
   - `alice_id_card.pdf`
   - `bob_photo.png`
   - `bob_resume.pdf`
3. If you want to add new students, simply copy their photos (`.png`, `.jpg`) and documents (`.pdf`) into `C:\student_portal_uploads` and insert matching filenames in MySQL.

---

## 🔨 Step 4: Build the Project with Maven

In the project folder (`exp-3`), open PowerShell or Command Prompt and run:

```powershell
.\mvnw.cmd clean package
```
*(Or `mvn clean package` if you have Maven in your PATH)*

This will generate the deployment package:
`target/StudentProfilePortal.war`

---

## 🚀 Step 5: Deploy and Run on Apache Tomcat 10.1

### Method A: Standalone Apache Tomcat 10.1
1. Download **Apache Tomcat 10.1.x (Core zip)** from [tomcat.apache.org](https://tomcat.apache.org/download-10.cgi).
2. Extract it (e.g. `C:\apache-tomcat-10.1.34`).
3. Copy `target/StudentProfilePortal.war` into Tomcat's `webapps/` folder:
   ```powershell
   Copy-Item target\StudentProfilePortal.war C:\apache-tomcat-10.1.34\webapps\
   ```
4. Start Tomcat by executing `bin/startup.bat`:
   ```powershell
   C:\apache-tomcat-10.1.34\bin\startup.bat
   ```
5. Open your web browser and navigate to:
   ```text
   http://localhost:8080/StudentProfilePortal
   ```

### Method B: Using Eclipse IDE / IntelliJ IDEA / VS Code
* **IntelliJ IDEA Ultimate:** Open the project folder -> Add Configuration -> Tomcat Server -> Local -> Select your Tomcat 10.1 home -> Under Deployment tab, click `+` -> Artifact -> `StudentProfilePortal:war` -> Run.
* **Eclipse IDE:** File -> Import -> Existing Maven Projects -> Right-click project -> Run As -> Run on Server -> Select Tomcat v10.1.
* **VS Code:** Install "Community Server Connectors" extension -> Add Apache Tomcat 10.1 -> Deploy `StudentProfilePortal.war`.

---

## 🔑 Login Credentials

Use any of these demo accounts:

| Username | Password | Role | Description |
| :--- | :--- | :--- | :--- |
| `admin` | `admin123` | Administrator | Full access to portal |
| `faculty` | `faculty123` | Faculty | Full access to portal |

---

## 🎯 Verification Checklist

1. **Authentication & Session:**
   - Navigating directly to `http://localhost:8080/StudentProfilePortal/students` redirects unauthorized users back to `login.jsp`.
   - Logging in with invalid password shows a clean red alert banner.
   - Logging in with `admin` / `admin123` creates an `HttpSession` and opens the profile dashboard.
2. **Student Profile Listing (JSP & JDBC):**
   - Student profiles for Alice, Bob, and Charlie are fetched dynamically using JDBC `PreparedStatement` and rendered in responsive cards.
3. **Secure File Streaming (FileServlet):**
   - Photos for Alice and Bob are loaded via `<img src=".../file?name=alice_photo.png&type=photo">`.
   - Clicking "📄 View PDF Document" opens the student's ID card/resume PDF inline in a new browser tab.
4. **Conditionals & Missing Handling:**
   - Charlie Brown has no photo and no document: shows "No Photo Uploaded" placeholder and yellow badge "⚠️ No document attached".
5. **Session Invalidation (Logout):**
   - Clicking the "🚪 Logout" button invalidates the session via `LogoutServlet` and redirects back to `login.jsp` with a success notice.
