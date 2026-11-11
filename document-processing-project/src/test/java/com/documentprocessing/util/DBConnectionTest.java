package com.documentprocessing.util;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DBConnectionTest {

    @Test
    void shouldEstablishDatabaseConnectionWithValidCredentials() throws SQLException {

        Connection mockConnection = mock(Connection.class);

        DBConnection.ConnectionProvider provider = mock(
                DBConnection.ConnectionProvider.class);

        when(provider.getConnection(
                "jdbc:postgresql://localhost:5432/document_processing_db",
                "postgres",
                "password"
        )).thenReturn(mockConnection);

        Connection result = DBConnection.getConnection(
                "localhost",
                "5432",
                "document_processing_db",
                "postgres",
                "password",
                provider
        );

        assertSame(mockConnection, result);

        verify(provider).getConnection(
                "jdbc:postgresql://localhost:5432/document_processing_db",
                "postgres",
                "password"
        );
    }
}