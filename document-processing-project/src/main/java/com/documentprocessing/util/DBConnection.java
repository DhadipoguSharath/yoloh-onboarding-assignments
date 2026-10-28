package com.documentprocessing.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    public static Connection getConnection() throws SQLException {

        String host = System.getenv("DB_HOST");
        String port = System.getenv("DB_PORT");
        String database = System.getenv("DB_NAME");
        String username = System.getenv("DB_USERNAME");
        String password = System.getenv("DB_PASSWORD");

        if (host == null || host.isBlank()
                || port == null || port.isBlank()
                || database == null || database.isBlank()
                || username == null || username.isBlank()
                || password == null || password.isBlank()) {

            throw new IllegalStateException(
                    "Required database environment variables are missing");
        }

        String jdbcUrl =
                "jdbc:postgresql://" + host + ":" + port + "/" + database;

        return DriverManager.getConnection(
                jdbcUrl, username, password);
    }
}