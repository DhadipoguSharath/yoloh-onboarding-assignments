package com.employee.dao;

import com.employee.model.Employee;
import java.util.List;


public interface EmployeeDAO {

    public void addEmployee(Employee employee);

    public void updateEmployee(Employee employee);

    public void deleteEmployee(int employeeId);

    public Employee getEmployeeById(int employeeId);

    public List<Employee> getAllEmployees();

}