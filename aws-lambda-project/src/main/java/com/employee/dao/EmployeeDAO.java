package com.employee.dao;

import com.employee.model.Employee;
import java.util.List;

public interface EmployeeDAO {

    Employee addEmployee(Employee employee);

    Employee updateEmployee(Employee employee);

    boolean deleteEmployee(int employeeId);

    Employee getEmployeeById(int employeeId);

    List<Employee> getAllEmployees();
}
