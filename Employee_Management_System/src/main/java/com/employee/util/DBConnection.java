package com.employee.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL = System.getenv("URL");
    private static final String USERNAME = System.getenv("USERNAME");
    private static final String PASSWORD = System.getenv("PASSWORD");

    private DBConnection() {
        // Prevent object creation for this utility class.
    }

    public static Connection getConnection() throws SQLException {

        if (URL == null || URL.isBlank()) {
            throw new SQLException("Database URL environment variable is not configured.");
        }

        if (USERNAME == null || USERNAME.isBlank()) {
            throw new SQLException("Database username environment variable is not configured.");
        }

        if (PASSWORD == null || PASSWORD.isBlank()) {
            throw new SQLException("Database password environment variable is not configured.");
        }

        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }
}