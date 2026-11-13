package com.employee.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private DBConnection() {
        // Prevent object creation for this utility class.
    }

    public static Connection getConnection() throws SQLException {
        return getConnection(
                System.getenv("DB_URL"),
                System.getenv("DB_USERNAME"),
                System.getenv("DB_PASSWORD")
        );
    }

    public static Connection getConnection(
            String url, String username, String password
    ) throws SQLException {

        if (url == null || url.isBlank()) {
            throw new SQLException("Database URL is not configured.");
        }

        if (username == null || username.isBlank()) {
            throw new SQLException("Database username is not configured.");
        }

        if (password == null || password.isBlank()) {
            throw new SQLException("Database password is not configured.");
        }

        return DriverManager.getConnection(url, username, password);
    }
}