package com.documentprocessing.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    public static Connection getConnection() throws SQLException {

        return getConnection(
                System.getenv("DB_HOST"),
                System.getenv("DB_PORT"),
                System.getenv("DB_NAME"),
                System.getenv("DB_USERNAME"),
                System.getenv("DB_PASSWORD"),
                DriverManager::getConnection
        );
    }

    static Connection getConnection(
            String host,
            String port,
            String database,
            String username,
            String password,
            ConnectionProvider provider) throws SQLException {

        if (host == null || host.isBlank()
                || port == null || port.isBlank()
                || database == null || database.isBlank()
                || username == null || username.isBlank()
                || password == null || password.isBlank()) {

            throw new IllegalStateException(
                    "Required database environment variables are missing");
        }

        String jdbcUrl
                = "jdbc:postgresql://" + host + ":" + port + "/" + database;

        return provider.getConnection(jdbcUrl, username, password);
    }

    @FunctionalInterface
    interface ConnectionProvider {

        Connection getConnection(
                String url,
                String username,
                String password) throws SQLException;
    }
}
