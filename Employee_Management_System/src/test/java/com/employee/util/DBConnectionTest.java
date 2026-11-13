package com.employee.util;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

class DBConnectionTest {

    @Test
    void shouldThrowExceptionWhenUrlIsNull() {
        assertThrows(SQLException.class, () -> DBConnection.getConnection(null, "postgres", "password"));
    }

    @Test
    void shouldThrowExceptionWhenUrlIsEmpty() {
        assertThrows(SQLException.class, () -> DBConnection.getConnection("", "postgres", "password"));
    }

    @Test
    void shouldThrowExceptionWhenUsernameIsNull() {
        assertThrows(SQLException.class, () -> DBConnection.getConnection("jdbc:postgresql://localhost:5432/test", null, "password"));
    }

    @Test
    void shouldThrowExceptionWhenUsernameIsEmpty() {
        assertThrows(SQLException.class, () -> DBConnection.getConnection("jdbc:postgresql://localhost:5432/test", "", "password"));
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsNull() {
        assertThrows(SQLException.class, () -> DBConnection.getConnection("jdbc:postgresql://localhost:5432/test", "postgres", null));
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsEmpty() {
        assertThrows(SQLException.class, () -> DBConnection.getConnection("jdbc:postgresql://localhost:5432/test", "postgres", ""));
    }

    @Test
    void shouldThrowExceptionWhenDatabaseUrlIsInvalid() {
        assertThrows(SQLException.class, () -> DBConnection.getConnection("invalid-url", "postgres", "password"));
    }

    @Test
    void shouldThrowExceptionWhenDatabaseCredentialsAreIncorrect() {
        assertThrows(SQLException.class, () -> DBConnection.getConnection("jdbc:postgresql://localhost:5432/postgres", "wrong_user", "wrong_password"));
    }

    @Test
    void shouldThrowExceptionWhenDatabaseIsUnavailable() {
        assertThrows(SQLException.class,() -> DBConnection.getConnection("jdbc:postgresql://localhost:9999/test","postgres","password"));
    }
}
