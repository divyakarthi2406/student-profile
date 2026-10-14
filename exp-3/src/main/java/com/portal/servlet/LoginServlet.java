package com.portal.servlet;

import com.portal.util.DBConnection;
import com.portal.util.PasswordUtil;

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

/**
 * Servlet handling user authentication and HTTP session creation.
 * Securely hashes entered passwords using SHA-256 before verification.
 */
@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // If already logged in, redirect directly to the student portal
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            response.sendRedirect(request.getContextPath() + "/students");
            return;
        }

        // Otherwise display the login page
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        // Input validation
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Please provide both username and password.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        username = username.trim();

        // SQL Query using PreparedStatement to prevent SQL Injection
        String sql = "SELECT id, username, password, role FROM users WHERE username = ?";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            rs = stmt.executeQuery();

            if (rs.next()) {
                String storedHashedPassword = rs.getString("password");
                String userRole = rs.getString("role");

                // Verify entered password against the stored SHA-256 hash
                if (PasswordUtil.verifyPassword(password, storedHashedPassword)) {
                    // Password matched! Invalidate any previous session for security
                    HttpSession oldSession = request.getSession(false);
                    if (oldSession != null) {
                        oldSession.invalidate();
                    }

                    // Create new HTTP Session
                    HttpSession newSession = request.getSession(true);
                    newSession.setAttribute("user", username);
                    newSession.setAttribute("role", userRole);
                    newSession.setMaxInactiveInterval(30 * 60); // 30 minutes session timeout

                    // Redirect to StudentListServlet
                    response.sendRedirect(request.getContextPath() + "/students");
                    return;
                }
            }

            // If credentials do not match or user is not found
            request.setAttribute("errorMessage", "Invalid username or password. Please try again.");
            request.setAttribute("enteredUsername", username);
            request.getRequestDispatcher("/login.jsp").forward(request, response);

        } catch (SQLException e) {
            System.err.println("[LoginServlet] Database error during login: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("errorMessage", "Database connection error. Please verify MySQL service and credentials.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        } finally {
            DBConnection.close(conn, stmt, rs);
        }
    }
}
