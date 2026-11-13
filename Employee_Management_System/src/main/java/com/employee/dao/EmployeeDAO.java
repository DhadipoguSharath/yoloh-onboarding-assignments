package com.employee.dao;

import com.employee.model.Employee;
import com.employee.exception.EmployeeNotFoundException;
import com.employee.exception.InvalidEmployeeDataException;

import java.sql.SQLException;
import java.util.List;

public interface EmployeeDAO {

    void addEmployee(Employee employee)
            throws SQLException, InvalidEmployeeDataException;

    void updateEmployee(Employee employee)
            throws SQLException, EmployeeNotFoundException,
                   InvalidEmployeeDataException;

    void deleteEmployee(int employeeId)
            throws SQLException, EmployeeNotFoundException;

    Employee getEmployeeById(int employeeId)
            throws SQLException, EmployeeNotFoundException;

    List<Employee> getAllEmployees() throws SQLException;
}