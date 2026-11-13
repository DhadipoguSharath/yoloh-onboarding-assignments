package com.employee.dao;

import com.employee.exception.EmployeeNotFoundException;
import com.employee.exception.InvalidEmployeeDataException;
import com.employee.model.Employee;
import com.employee.model.EmployeeStatus;
import com.employee.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAOImpl implements EmployeeDAO {

    private static final String INSERT_QUERY =
            "INSERT INTO employees (name, email, department, status) VALUES (?, ?, ?, ?)";

    private static final String UPDATE_QUERY =
            "UPDATE employees SET name = ?, email = ?, department = ?, status = ? WHERE employee_id = ?";

    private static final String DELETE_QUERY =
            "DELETE FROM employees WHERE employee_id = ?";

    private static final String SELECT_BY_ID_QUERY =
            "SELECT * FROM employees WHERE employee_id = ?";

    private static final String SELECT_ALL_QUERY =
            "SELECT * FROM employees";

    @Override
    public void addEmployee(Employee employee)
            throws SQLException, InvalidEmployeeDataException {

        if (employee == null) {
            throw new InvalidEmployeeDataException("Employee data cannot be null.");
        }

        String sql = INSERT_QUERY;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, employee.getName());
            pstmt.setString(2, employee.getEmail());
            pstmt.setString(3, employee.getDepartment());
            pstmt.setString(4, employee.getStatus().name());

            pstmt.executeUpdate();
        }
    }

    @Override
    public void updateEmployee(Employee employee)
            throws SQLException, EmployeeNotFoundException,
                   InvalidEmployeeDataException {

        if (employee == null) {
            throw new InvalidEmployeeDataException("Employee data cannot be null.");
        }

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(UPDATE_QUERY)) {

            pstmt.setString(1, employee.getName());
            pstmt.setString(2, employee.getEmail());
            pstmt.setString(3, employee.getDepartment());
            pstmt.setString(4, employee.getStatus().name());
            pstmt.setInt(5, employee.getEmployeeId());

            int rowsUpdated = pstmt.executeUpdate();

            if (rowsUpdated == 0) {
                throw new EmployeeNotFoundException(
                        "Employee not found with ID: " + employee.getEmployeeId());
            }
        }
    }

    @Override
    public void deleteEmployee(int employeeId)
            throws SQLException, EmployeeNotFoundException {

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(DELETE_QUERY)) {

            pstmt.setInt(1, employeeId);

            int rowsDeleted = pstmt.executeUpdate();

            if (rowsDeleted == 0) {
                throw new EmployeeNotFoundException(
                        "Employee not found with ID: " + employeeId);
            }
        }
    }

    @Override
    public Employee getEmployeeById(int employeeId)
            throws SQLException, EmployeeNotFoundException {

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(SELECT_BY_ID_QUERY)) {

            pstmt.setInt(1, employeeId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapEmployee(rs);
                }
            }
        }

        throw new EmployeeNotFoundException(
                "Employee not found with ID: " + employeeId);
    }

    @Override
    public List<Employee> getAllEmployees() throws SQLException {

        List<Employee> employees = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(SELECT_ALL_QUERY);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                employees.add(mapEmployee(rs));
            }
        }

        return employees;
    }

    private Employee mapEmployee(ResultSet rs) throws SQLException {

        int employeeId = rs.getInt("employee_id");
        String name = rs.getString("name");
        String email = rs.getString("email");
        String department = rs.getString("department");

        EmployeeStatus status =
                EmployeeStatus.valueOf(rs.getString("status"));

        return new Employee(employeeId, name, email, department, status);
    }
}