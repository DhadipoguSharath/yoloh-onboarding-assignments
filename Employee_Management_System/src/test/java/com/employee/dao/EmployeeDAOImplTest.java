package com.employee.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.MockedStatic;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.employee.exception.EmployeeNotFoundException;
import com.employee.exception.InvalidEmployeeDataException;
import com.employee.model.Employee;
import com.employee.model.EmployeeStatus;
import com.employee.util.DBConnection;

class EmployeeDAOImplTest {
    private EmployeeDAOImpl employeeDAO;
    private Connection connection;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;

    @BeforeEach
    void setUp() throws SQLException {
        employeeDAO = new EmployeeDAOImpl();
        connection = mock(Connection.class);
        preparedStatement = mock(PreparedStatement.class);
        resultSet = mock(ResultSet.class);
    }

    @Test
    void shouldAddEmployeeSuccessfully() throws SQLException, InvalidEmployeeDataException {
        Employee employee = new Employee("Sharath", "sharath@gmail.com", "Engineering", EmployeeStatus.ACTIVE);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        try (MockedStatic<DBConnection> mockedDB = mockStatic(DBConnection.class)) {
            mockedDB.when(DBConnection::getConnection).thenReturn(connection);
            employeeDAO.addEmployee(employee);
            verify(preparedStatement).setString(1, "Sharath");
            verify(preparedStatement).setString(2, "sharath@gmail.com");
            verify(preparedStatement).setString(3, "Engineering");
            verify(preparedStatement).setString(4, "ACTIVE");
            verify(preparedStatement).executeUpdate();
        }
    }

    @Test
    void shouldThrowExceptionWhenAddingNullEmployee() {
        assertThrows(InvalidEmployeeDataException.class, () -> employeeDAO.addEmployee(null));
    }

    @Test
    void shouldUpdateEmployeeSuccessfully() throws SQLException, EmployeeNotFoundException, InvalidEmployeeDataException {
        Employee employee = new Employee(1, "Sharath", "sharath@gmail.com", "Engineering", EmployeeStatus.ACTIVE);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);
        try (MockedStatic<DBConnection> mockedDB = mockStatic(DBConnection.class)) {
            mockedDB.when(DBConnection::getConnection).thenReturn(connection);
            employeeDAO.updateEmployee(employee);
            verify(preparedStatement).setString(1, "Sharath");
            verify(preparedStatement).setString(2, "sharath@gmail.com");
            verify(preparedStatement).setString(3, "Engineering");
            verify(preparedStatement).setString(4, "ACTIVE");
            verify(preparedStatement).setInt(5, 1);
            verify(preparedStatement).executeUpdate();
        }
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNullEmployee() {
        assertThrows(InvalidEmployeeDataException.class, () -> employeeDAO.updateEmployee(null));
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingEmployee() throws SQLException, InvalidEmployeeDataException {
        Employee employee = new Employee(999, "Sharath", "sharath@gmail.com", "Engineering", EmployeeStatus.ACTIVE);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(0);
        try (MockedStatic<DBConnection> mockedDB = mockStatic(DBConnection.class)) {
            mockedDB.when(DBConnection::getConnection).thenReturn(connection);
            assertThrows(EmployeeNotFoundException.class, () -> employeeDAO.updateEmployee(employee));
        }
    }

    @Test
    void shouldDeleteEmployeeSuccessfully() throws SQLException, EmployeeNotFoundException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);
        try (MockedStatic<DBConnection> mockedDB = mockStatic(DBConnection.class)) {
            mockedDB.when(DBConnection::getConnection).thenReturn(connection);
            employeeDAO.deleteEmployee(1);
            verify(preparedStatement).setInt(1, 1);
            verify(preparedStatement).executeUpdate();
        }
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistingEmployee() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(0);
        try (MockedStatic<DBConnection> mockedDB = mockStatic(DBConnection.class)) {
            mockedDB.when(DBConnection::getConnection).thenReturn(connection);
            assertThrows(EmployeeNotFoundException.class, () -> employeeDAO.deleteEmployee(999));
        }
    }

    @Test
    void shouldGetEmployeeByIdSuccessfully() throws SQLException, EmployeeNotFoundException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt("employee_id")).thenReturn(1);
        when(resultSet.getString("name")).thenReturn("Sharath");
        when(resultSet.getString("email")).thenReturn("sharath@gmail.com");
        when(resultSet.getString("department")).thenReturn("Engineering");
        when(resultSet.getString("status")).thenReturn("ACTIVE");
        try (MockedStatic<DBConnection> mockedDB = mockStatic(DBConnection.class)) {
            mockedDB.when(DBConnection::getConnection).thenReturn(connection);
            Employee employee = employeeDAO.getEmployeeById(1);
            assertEquals(1, employee.getEmployeeId());
            assertEquals("Sharath", employee.getName());
            assertEquals("sharath@gmail.com", employee.getEmail());
            assertEquals("Engineering", employee.getDepartment());
            assertEquals(EmployeeStatus.ACTIVE, employee.getStatus());
            verify(preparedStatement).setInt(1, 1);
        }
    }

    @Test
    void shouldThrowExceptionWhenEmployeeIdDoesNotExist() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        try (MockedStatic<DBConnection> mockedDB = mockStatic(DBConnection.class)) {
            mockedDB.when(DBConnection::getConnection).thenReturn(connection);
            assertThrows(EmployeeNotFoundException.class, () -> employeeDAO.getEmployeeById(999));
        }
    }

    @Test
    void shouldGetAllEmployeesSuccessfully() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, true, false);
        when(resultSet.getInt("employee_id")).thenReturn(1, 2);
        when(resultSet.getString("name")).thenReturn("Sharath", "Rahul");
        when(resultSet.getString("email")).thenReturn("sharath@gmail.com", "rahul@gmail.com");
        when(resultSet.getString("department")).thenReturn("Engineering", "HR");
        when(resultSet.getString("status")).thenReturn("ACTIVE", "INACTIVE");
        try (MockedStatic<DBConnection> mockedDB = mockStatic(DBConnection.class)) {
            mockedDB.when(DBConnection::getConnection).thenReturn(connection);
            List<Employee> employees = employeeDAO.getAllEmployees();
            assertEquals(2, employees.size());
            assertEquals("Sharath", employees.get(0).getName());
            assertEquals("Rahul", employees.get(1).getName());
        }
    }

    @Test
    void shouldReturnEmptyListWhenNoEmployeesExist() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        try (MockedStatic<DBConnection> mockedDB = mockStatic(DBConnection.class)) {
            mockedDB.when(DBConnection::getConnection).thenReturn(connection);
            List<Employee> employees = employeeDAO.getAllEmployees();
            assertTrue(employees.isEmpty());
        }
    }

    @Test
    void shouldPropagateSQLExceptionWhenAddingEmployee() throws InvalidEmployeeDataException, SQLException {
        Employee employee = new Employee("Sharath", "sharath@gmail.com", "Engineering", EmployeeStatus.ACTIVE);
        when(connection.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));
        try (MockedStatic<DBConnection> mockedDB = mockStatic(DBConnection.class)) {
            mockedDB.when(DBConnection::getConnection).thenReturn(connection);
            assertThrows(SQLException.class, () -> employeeDAO.addEmployee(employee));
        }
    }

    @Test
    void shouldPropagateSQLExceptionWhenUpdatingEmployee() throws InvalidEmployeeDataException, SQLException {
        Employee employee = new Employee(1, "Sharath", "sharath@gmail.com", "Engineering", EmployeeStatus.ACTIVE);
        when(connection.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));
        try (MockedStatic<DBConnection> mockedDB = mockStatic(DBConnection.class)) {
            mockedDB.when(DBConnection::getConnection).thenReturn(connection);
            assertThrows(SQLException.class, () -> employeeDAO.updateEmployee(employee));
        }
    }

    @Test
    void shouldPropagateSQLExceptionWhenDeletingEmployee() throws SQLException {
        when(connection.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));
        try (MockedStatic<DBConnection> mockedDB = mockStatic(DBConnection.class)) {
            mockedDB.when(DBConnection::getConnection).thenReturn(connection);
            assertThrows(SQLException.class, () -> employeeDAO.deleteEmployee(1));
        }
    }

    @Test
    void shouldPropagateSQLExceptionWhenGettingEmployeeById() throws SQLException {
        when(connection.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));
        try (MockedStatic<DBConnection> mockedDB = mockStatic(DBConnection.class)) {
            mockedDB.when(DBConnection::getConnection).thenReturn(connection);
            assertThrows(SQLException.class, () -> employeeDAO.getEmployeeById(1));
        }
    }

    @Test
    void shouldPropagateSQLExceptionWhenGettingAllEmployees() throws SQLException {
        when(connection.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));
        try (MockedStatic<DBConnection> mockedDB = mockStatic(DBConnection.class)) {
            mockedDB.when(DBConnection::getConnection).thenReturn(connection);
            assertThrows(SQLException.class, () -> employeeDAO.getAllEmployees());
        }
    }
            
    @Test
    void shouldThrowExceptionWhenDatabaseReturnsInvalidEmployeeStatus() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt("employee_id")).thenReturn(1);
        when(resultSet.getString("name")).thenReturn("Sharath");
        when(resultSet.getString("email")).thenReturn("sharath@gmail.com");  
        when(resultSet.getString("department")).thenReturn("Engineering");
        when(resultSet.getString("status")).thenReturn("PENDING");
        try (MockedStatic<DBConnection> mockedDB = mockStatic(DBConnection.class)) {
            mockedDB.when(DBConnection::getConnection).thenReturn(connection);
            assertThrows(IllegalArgumentException.class,() -> employeeDAO.getEmployeeById(1));
        }
    }
}          