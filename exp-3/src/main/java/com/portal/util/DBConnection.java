package com.portal.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Utility class to manage database connections to MySQL using JDBC.
 * Reads connection settings from db.properties if available, with safe defaults.
 */
public class DBConnection {

    private static String jdbcDriver = "com.mysql.cj.jdbc.Driver";
    private static String jdbcUrl = "jdbc:mysql://localhost:3306/student_portal?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static String jdbcUser = "root";
    private static String jdbcPassword = "root";
    private static String uploadDir = "C:/student_portal_uploads";

    static {
        // Load properties from classpath resource
        try (InputStream input = DBConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input != null) {
                Properties prop = new Properties();
                prop.load(input);

                jdbcDriver = prop.getProperty("db.driver", jdbcDriver);
                jdbcUrl = prop.getProperty("db.url", jdbcUrl);
                jdbcUser = prop.getProperty("db.user", jdbcUser);
                jdbcPassword = prop.getProperty("db.password", jdbcPassword);
                uploadDir = prop.getProperty("upload.dir", uploadDir);
                System.out.println("[DBConnection] Configuration loaded successfully from db.properties.");
            } else {
                System.out.println("[DBConnection] db.properties not found on classpath, using built-in defaults.");
            }
        } catch (Exception e) {
            System.err.println("[DBConnection] Failed to load db.properties, using defaults: " + e.getMessage());
        }

        // Register JDBC driver class
        try {
            Class.forName(jdbcDriver);
            System.out.println("[DBConnection] MySQL JDBC Driver registered successfully: " + jdbcDriver);
        } catch (ClassNotFoundException e) {
            System.err.println("[DBConnection] ERROR: MySQL JDBC Driver not found! Ensure mysql-connector-j is in classpath: " + e.getMessage());
        }
    }

    /**
     * Establishes and returns a new connection to the MySQL student_portal database.
     *
     * @return Active Connection object
     * @throws SQLException If database connection fails
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPassword);
    }

    /**
     * Returns the configured directory path for file uploads (student photos and documents).
     *
     * @return Directory path string
     */
    public static String getUploadDir() {
        return uploadDir;
    }

    /**
     * Safe resource cleanup for ResultSet.
     */
    public static void close(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException ignored) {
            }
        }
    }

    /**
     * Safe resource cleanup for Statement / PreparedStatement.
     */
    public static void close(Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException ignored) {
            }
        }
    }

    /**
     * Safe resource cleanup for Connection.
     */
    public static void close(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException ignored) {
            }
        }
    }

    /**
     * Safe resource cleanup for Connection, Statement, and ResultSet together.
     */
    public static void close(Connection conn, Statement stmt, ResultSet rs) {
        close(rs);
        close(stmt);
        close(conn);
    }
}
