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
 * Servlet handling student deletion.
 * Protected by Role-Based Access Control (RBAC): Only 'admin' role may delete records.
 */
@WebServlet(name = "DeleteStudentServlet", urlPatterns = {"/delete-student"})
public class DeleteStudentServlet extends HttpServlet {
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
            // Block non-admins (such as faculty)
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: Only administrators can delete records.");
            return;
        }

        // 2. Retrieve student ID
        String idParam = request.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/students");
            return;
        }

        try {
            int studentId = Integer.parseInt(idParam.trim());

            // 3. Delete from MySQL using PreparedStatement
            String sql = "DELETE FROM students WHERE id = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                
                stmt.setInt(1, studentId);
                stmt.executeUpdate();
            }

            response.sendRedirect(request.getContextPath() + "/students?deleted=true");

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/students");
        } catch (SQLException e) {
            System.err.println("[DeleteStudentServlet] Error deleting student: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/students?error=delete_failed");
        }
    }
}
