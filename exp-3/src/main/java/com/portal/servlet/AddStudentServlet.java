package com.portal.servlet;

import com.portal.util.DBConnection;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Servlet handling adding new student records.
 * Protected by Role-Based Access Control (RBAC): Only 'admin' role may add records.
 */
@WebServlet(name = "AddStudentServlet", urlPatterns = {"/add-student"})
public class AddStudentServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. RBAC Authentication & Authorization Check
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String role = (String) session.getAttribute("role");
        if (!"admin".equalsIgnoreCase(role)) {
            // Faculty or non-admin users are strictly blocked
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: Only administrators can add new students.");
            return;
        }

        // 2. Retrieve form fields
        String rollNo = request.getParameter("roll_no");
        String name = request.getParameter("name");
        String department = request.getParameter("department");
        String email = request.getParameter("email");
        String photoPath = request.getParameter("photo_path");
        String documentPath = request.getParameter("document_path");

        if (rollNo == null || rollNo.trim().isEmpty() ||
            name == null || name.trim().isEmpty() ||
            department == null || department.trim().isEmpty() ||
            email == null || email.trim().isEmpty()) {
            
            request.setAttribute("errorMessage", "All required fields (Roll No, Name, Dept, Email) must be provided.");
            request.getRequestDispatcher("/students").forward(request, response);
            return;
        }

        rollNo = rollNo.trim();
        name = name.trim();
        department = department.trim();
        email = email.trim();
        photoPath = (photoPath != null && !photoPath.trim().isEmpty()) ? photoPath.trim() : null;
        documentPath = (documentPath != null && !documentPath.trim().isEmpty()) ? documentPath.trim() : null;

        // 3. Insert into MySQL students table using PreparedStatement
        String sql = "INSERT INTO students (roll_no, name, department, email, photo_path, document_path) VALUES (?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, rollNo);
            stmt.setString(2, name);
            stmt.setString(3, department);
            stmt.setString(4, email);
            stmt.setString(5, photoPath);
            stmt.setString(6, documentPath);

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                // Success! Redirect to student list with success notice
                response.sendRedirect(request.getContextPath() + "/students?added=true");
                return;
            } else {
                request.setAttribute("errorMessage", "Failed to add student record.");
                request.getRequestDispatcher("/students").forward(request, response);
            }

        } catch (SQLException e) {
            System.err.println("[AddStudentServlet] Error inserting student: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("errorMessage", "Database Error: " + e.getMessage());
            request.getRequestDispatcher("/students").forward(request, response);
        } finally {
            DBConnection.close(conn, stmt, null);
        }
    }
}
