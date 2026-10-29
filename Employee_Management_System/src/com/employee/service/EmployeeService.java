package com.employee.service;

import com.employee.dao.EmployeeDAO;
import com.employee.dao.EmployeeDAOImpl;
import com.employee.exception.EmployeeNotFoundException;
import com.employee.exception.InvalidEmployeeDataException;
import com.employee.model.Employee;

import java.sql.SQLException;
import java.util.List;

public class EmployeeService {

    private final EmployeeDAO employeeDAO;

    public EmployeeService() {
        this.employeeDAO = new EmployeeDAOImpl();
    }

    // Constructor injection for testing or alternative DAO implementations
    public EmployeeService(EmployeeDAO employeeDAO) {
        this.employeeDAO = employeeDAO;
    }

    public void addEmployee(Employee employee)
            throws SQLException, InvalidEmployeeDataException {

        validateEmployee(employee);
        employeeDAO.addEmployee(employee);
    }

    public void updateEmployee(Employee employee)
            throws SQLException, EmployeeNotFoundException,
                   InvalidEmployeeDataException {

        validateEmployee(employee);

        if (employee.getEmployeeId() <= 0) {
            throw new InvalidEmployeeDataException(
                    "Employee ID must be greater than zero.");
        }

        employeeDAO.updateEmployee(employee);
    }

    public void deleteEmployee(int employeeId)
            throws SQLException, EmployeeNotFoundException {

        if (employeeId <= 0) {
            throw new IllegalArgumentException(
                    "Employee ID must be greater than zero.");
        }

        employeeDAO.deleteEmployee(employeeId);
    }

    public Employee getEmployeeById(int employeeId)
            throws SQLException, EmployeeNotFoundException {

        if (employeeId <= 0) {
            throw new IllegalArgumentException(
                    "Employee ID must be greater than zero.");
        }

        return employeeDAO.getEmployeeById(employeeId);
    }

    public List<Employee> getAllEmployees() throws SQLException {
        return employeeDAO.getAllEmployees();
    }

    private void validateEmployee(Employee employee)
            throws InvalidEmployeeDataException {

        if (employee == null) {
            throw new InvalidEmployeeDataException(
                    "Employee data cannot be null.");
        }

        if (isBlank(employee.getName())) {
            throw new InvalidEmployeeDataException(
                    "Employee name cannot be empty.");
        }

        if (isBlank(employee.getEmail())) {
            throw new InvalidEmployeeDataException(
                    "Employee email cannot be empty.");
        }

        if (!employee.getEmail().matches(
                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new InvalidEmployeeDataException(
                    "Please provide a valid email address.");
        }

        if (isBlank(employee.getDepartment())) {
            throw new InvalidEmployeeDataException(
                    "Employee department cannot be empty.");
        }

        if (employee.getStatus() == null) {
            throw new InvalidEmployeeDataException(
                    "Employee status cannot be null.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}