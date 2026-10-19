package com.employee.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.employee.model.Employee;
import com.employee.util.DBConnection;

public class PostgresEmployeeDAO implements EmployeeDAO {

    private static final String INSERT_QUERY = "INSERT INTO employees (name, email, department, status) " + "VALUES (?, ?, ?, ?) RETURNING employee_id";

    private static final String UPDATE_QUERY = "UPDATE employees SET name = ?, email = ?, department = ?, status = ? " + "WHERE employee_id = ?";

    private static final String DELETE_QUERY = "DELETE FROM employees WHERE employee_id = ?";

    private static final String SELECT_BY_ID_QUERY = "SELECT employee_id, name, email, department, status " + "FROM employees WHERE employee_id = ?";

    private static final String SELECT_ALL_QUERY = "SELECT employee_id, name, email, department, status " + "FROM employees ORDER BY employee_id";

    @Override
    public Employee addEmployee(Employee employee) {
        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement
                = connection.prepareStatement(INSERT_QUERY)) {

            statement.setString(1, employee.getName());
            statement.setString(2, employee.getEmail());
            statement.setString(3, employee.getDepartment());
            statement.setString(4, employee.getStatus());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    employee.setEmployeeId(resultSet.getInt("employee_id"));
                    return employee;
                }
            }

            throw new SQLException("Employee was not created.");

        } catch (SQLException e) {
            throw new RuntimeException("Failed to create employee: " + e.getMessage(), e);
        }

    }

    @Override
    public Employee updateEmployee(Employee employee) {
        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement
                = connection.prepareStatement(UPDATE_QUERY)) {

            statement.setString(1, employee.getName());
            statement.setString(2, employee.getEmail());
            statement.setString(3, employee.getDepartment());
            statement.setString(4, employee.getStatus());
            statement.setInt(5, employee.getEmployeeId());

            int rows = statement.executeUpdate();

            if (rows == 0) {
                return null;
            }

            return employee;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to update employee.", e);
        }
    }

    @Override
    public boolean deleteEmployee(int employeeId) {
        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement
                = connection.prepareStatement(DELETE_QUERY)) {

            statement.setInt(1, employeeId);
            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete employee.", e);
        }
    }

    @Override
    public Employee getEmployeeById(int employeeId) {
        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement
                = connection.prepareStatement(SELECT_BY_ID_QUERY)) {

            statement.setInt(1, employeeId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapEmployee(resultSet);
                }

                return null;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve employee.", e);
        }
    }

    @Override
    public java.util.List<Employee> getAllEmployees() {
        java.util.List<Employee> employees = new java.util.ArrayList<>();

        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement
                = connection.prepareStatement(SELECT_ALL_QUERY); ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                employees.add(mapEmployee(resultSet));
            }

            return employees;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve employees.", e);
        }
    }

    private Employee mapEmployee(ResultSet resultSet) throws SQLException {
        return new Employee(
                resultSet.getInt("employee_id"),
                resultSet.getString("name"),
                resultSet.getString("email"),
                resultSet.getString("department"),
                resultSet.getString("status")
        );
    }
}
