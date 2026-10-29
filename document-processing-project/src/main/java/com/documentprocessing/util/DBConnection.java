package com.documentprocessing.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static Connection getConnection() throws SQLException {

        String host = System.getenv("DB_HOST");
        String port = System.getenv("DB_PORT");
        String database = System.getenv("DB_NAME");
        String secretArn = System.getenv("DB_SECRET_ARN");
        String region = System.getenv("AWS_REGION");

        if (host == null || host.isBlank()
                || port == null || port.isBlank()
                || database == null || database.isBlank()
                || secretArn == null || secretArn.isBlank()
                || region == null || region.isBlank()) {
            throw new IllegalStateException(
                    "Required database configuration is missing");
        }

        try (SecretsManagerClient secretsClient =
                     SecretsManagerClient.builder()
                             .region(Region.of(region))
                             .build()) {

            GetSecretValueResponse response = secretsClient.getSecretValue(
                    GetSecretValueRequest.builder()
                            .secretId(secretArn)
                            .build());

            JsonNode secret = OBJECT_MAPPER.readTree(response.secretString());

            String username = secret.path("username").asText();
            String password = secret.path("password").asText();

            if (username.isBlank() || password.isBlank()) {
                throw new IllegalStateException(
                        "Database credentials are missing from the secret");
            }

            String jdbcUrl =
                    "jdbc:postgresql://" + host + ":" + port + "/" + database;

            return DriverManager.getConnection(jdbcUrl, username, password);

        } catch (Exception e) {
            throw new SQLException(
                    "Unable to retrieve database credentials or connect to the database",
                    e);
        }
    }
}
