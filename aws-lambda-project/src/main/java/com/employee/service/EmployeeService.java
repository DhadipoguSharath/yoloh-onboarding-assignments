package com.employee.service;

import java.util.List;

import com.employee.dao.EmployeeDAO;
import com.employee.model.Employee;

public class EmployeeService {

    private final EmployeeDAO employeeDAO;

    public EmployeeService(EmployeeDAO employeeDAO) {
        this.employeeDAO = employeeDAO;
    }

    public Employee addEmployee(Employee employee) {
        validateEmployee(employee);
        return employeeDAO.addEmployee(employee);
    }

    // Overloaded method to support path parameter ID from HTTP PUT requests
    public Employee updateEmployee(int employeeId, Employee employee) {
        if (employeeId <= 0) {
            throw new IllegalArgumentException(
                    "Employee ID must be greater than zero."
            );
        }
        if (employee == null) {
            throw new IllegalArgumentException(
                    "Employee data is required."
            );
        }

        employee.setEmployeeId(employeeId);
        return updateEmployee(employee);
    }

    public Employee updateEmployee(Employee employee) {
        if (employee == null || employee.getEmployeeId() <= 0) {
            throw new IllegalArgumentException(
                    "A valid employee ID is required."
            );
        }

        validateEmployee(employee);
        return employeeDAO.updateEmployee(employee);
    }

    public boolean deleteEmployee(int employeeId) {
        if (employeeId <= 0) {
            throw new IllegalArgumentException(
                    "Employee ID must be greater than zero."
            );
        }

        return employeeDAO.deleteEmployee(employeeId);
    }

    public Employee getEmployeeById(int employeeId) {
        if (employeeId <= 0) {
            throw new IllegalArgumentException(
                    "Employee ID must be greater than zero."
            );
        }

        return employeeDAO.getEmployeeById(employeeId);
    }

    public List<Employee> getAllEmployees() {
        return employeeDAO.getAllEmployees();
    }

    private void validateEmployee(Employee employee) {
        if (employee == null) {
            throw new IllegalArgumentException(
                    "Employee data is required."
            );
        }

        if (isBlank(employee.getName())) {
            throw new IllegalArgumentException(
                    "Employee name is required."
            );
        }

        if (isBlank(employee.getEmail())
                || !employee.getEmail().matches(
                        "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new IllegalArgumentException(
                    "A valid email address is required."
            );
        }

        if (isBlank(employee.getDepartment())) {
            throw new IllegalArgumentException(
                    "Department is required."
            );
        }

        if (isBlank(employee.getStatus())
                || !(employee.getStatus().equalsIgnoreCase("ACTIVE")
                || employee.getStatus().equalsIgnoreCase("INACTIVE"))) {
            throw new IllegalArgumentException(
                    "Status must be ACTIVE or INACTIVE."
            );
        }

        employee.setName(employee.getName().trim());
        employee.setEmail(employee.getEmail().trim());
        employee.setDepartment(employee.getDepartment().trim());
        employee.setStatus(employee.getStatus().toUpperCase().trim());
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
