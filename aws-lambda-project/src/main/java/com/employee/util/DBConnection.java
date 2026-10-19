
package com.employee.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

public class DBConnection {

    private static final String DB_URL = System.getenv("DB_URL");
    private static final String SECRET_ARN = System.getenv("DB_SECRET_ARN");

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static Connection getConnection() throws SQLException {

        if (DB_URL == null || DB_URL.isBlank()) {
            throw new SQLException("Missing DB_URL environment variable.");
        }

        if (SECRET_ARN == null || SECRET_ARN.isBlank()) {
            throw new SQLException("Missing DB_SECRET_ARN environment variable.");
        }

        try (SecretsManagerClient secretsClient =
                     SecretsManagerClient.builder()
                         .region(Region.EU_NORTH_1)
                         .build()) {

            GetSecretValueRequest request = GetSecretValueRequest.builder()
                .secretId(SECRET_ARN)
                .build();

            GetSecretValueResponse response =
                secretsClient.getSecretValue(request);

            JsonNode secret = OBJECT_MAPPER.readTree(response.secretString());

            String username = secret.path("username").asText();
            String password = secret.path("password").asText();

            if (username.isBlank() || password.isBlank()) {
                throw new SQLException(
                    "The Secrets Manager secret must contain username and password."
                );
            }

            return DriverManager.getConnection(DB_URL, username, password);

        } catch (SQLException e) {
            throw e;
        } catch (Exception e) {
            throw new SQLException(
                "Unable to retrieve database credentials or connect to PostgreSQL.",
                e
            );
        }
    }
}
