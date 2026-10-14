package com.portal.servlet;

import com.portal.model.Student;
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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Servlet to fetch multiple student records from MySQL using JDBC
 * and forward the result list to students.jsp.
 */
@WebServlet(name = "StudentListServlet", urlPatterns = {"/students"})
public class StudentListServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Session verification: Check if user is logged in
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            // Not authenticated: Redirect to login page
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        // 2. Fetch student records from database
        List<Student> studentList = new ArrayList<>();
        String sql = "SELECT id, roll_no, name, department, email, photo_path, document_path " +
                     "FROM students ORDER BY id ASC";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Student s = new Student(
                        rs.getInt("id"),
                        rs.getString("roll_no"),
                        rs.getString("name"),
                        rs.getString("department"),
                        rs.getString("email"),
                        rs.getString("photo_path"),
                        rs.getString("document_path")
                );
                studentList.add(s);
            }

            // Set the student list in the request attribute
            request.setAttribute("studentList", studentList);

        } catch (SQLException e) {
            System.err.println("[StudentListServlet] Database error while fetching students: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("errorMessage", "Error loading student records from database: " + e.getMessage());
        } finally {
            DBConnection.close(conn, stmt, rs);
        }

        // 3. Forward request to students.jsp
        request.getRequestDispatcher("/students.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
