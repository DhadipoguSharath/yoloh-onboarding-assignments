package com.documentprocessing.util;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DBConnectionTest {

    @Test
    void shouldThrowSQLExceptionWhenDatabaseConnectionFails() throws SQLException {

        DBConnection.ConnectionProvider provider = mock(
                DBConnection.ConnectionProvider.class);

        when(provider.getConnection(
                "jdbc:postgresql://localhost:5432/document_processing_db",
                "postgres",
                "password"
        )).thenThrow(new SQLException("Database connection failed"));

        SQLException exception = assertThrows(
                SQLException.class,
                () -> DBConnection.getConnection(
                        "localhost",
                        "5432",
                        "document_processing_db",
                        "postgres",
                        "password",
                        provider
                )
        );

        assertEquals("Database connection failed", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenDatabaseHostIsMissing() {

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> DBConnection.getConnection(
                        null, "5432", "document_processing_db",
                        "postgres", "password",
                        (url, username, password) -> null
                )
        );

        assertTrue(exception.getMessage().contains(
                "Required database environment variables are missing"));
    }

    @Test
    void shouldThrowExceptionWhenDatabasePortIsMissing() {

        assertThrows(
                IllegalStateException.class,
                () -> DBConnection.getConnection(
                        "localhost", "", "document_processing_db",
                        "postgres", "password",
                        (url, username, password) -> null
                )
        );
    }

    @Test
    void shouldThrowExceptionWhenDatabaseNameIsMissing() {

        assertThrows(
                IllegalStateException.class,
                () -> DBConnection.getConnection(
                        "localhost", "5432", "",
                        "postgres", "password",
                        (url, username, password) -> null
                )
        );
    }

    @Test
    void shouldThrowExceptionWhenUsernameIsMissing() {

        assertThrows(
                IllegalStateException.class,
                () -> DBConnection.getConnection(
                        "localhost", "5432", "document_processing_db",
                        null, "password",
                        (url, username, password) -> null
                )
        );
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsMissing() {

        assertThrows(
                IllegalStateException.class,
                () -> DBConnection.getConnection(
                        "localhost", "5432", "document_processing_db",
                        "postgres", " ",
                        (url, username, password) -> null
                )
        );
    }
}