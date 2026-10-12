package com.employee.service;

import com.employee.dao.EmployeeDAO;
import com.employee.model.Employee;
import com.employee.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmployeeService implements EmployeeDAO{
    

    private static final String INSERT_QUERY = "INSERT INTO employees (name, email, department, status) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE employees SET name = ?, email = ?, department = ?, status = ? WHERE employee_id = ?";
    private static final String DELETE_QUERY = "DELETE FROM employees WHERE employee_id = ?";
    private static final String SELECT_ALL_QUERY = "SELECT * FROM employees";
    
    public void addEmployee(Employee employee) {
        
        try(Connection connection = DBConnection.getConnection()) {
            PreparedStatement pstmt = connection.prepareStatement(INSERT_QUERY);

            pstmt.setString(1, employee.getName());
            pstmt.setString(2, employee.getEmail());
            pstmt.setString(3, employee.getDepartment());
            pstmt.setString(4, employee.getStatus());
            
            int rows = pstmt.executeUpdate();

            if(rows > 0) {
                System.out.println("Employee added successfully.");
            } else {
                System.out.println("Failed to add employee.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        
    }

    public void updateEmployee(Employee employee) {

        try(Connection connection = DBConnection.getConnection()) {
            PreparedStatement pstmt = connection.prepareStatement(UPDATE_QUERY);
            pstmt.setString(1, employee.getName());
            pstmt.setString(2, employee.getEmail());
            pstmt.setString(3, employee.getDepartment());
            pstmt.setString(4, employee.getStatus());
            pstmt.setInt(5, employee.getEmployeeId());
            int rows = pstmt.executeUpdate();

            if(rows > 0) {
                System.out.println("Employee updated successfully.");
            } else {
                System.out.println("Failed to update employee.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteEmployee(int employeeId) {
        try(Connection connection = DBConnection.getConnection()) {
            PreparedStatement pstmt = connection.prepareStatement(DELETE_QUERY);
            pstmt.setInt(1, employeeId);
            int rows = pstmt.executeUpdate();

            if(rows > 0) {
                System.out.println("Employee deleted successfully.");
            } else {
                System.out.println("Failed to delete employee.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public Employee getEmployeeById(int employeeId) {
        Employee employee = null;
        try(Connection connection = DBConnection.getConnection()) {
            PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM employees WHERE employee_id = ?");
            pstmt.setInt(1, employeeId);
            ResultSet rs = pstmt.executeQuery();

            if(rs.next()) {
                String name = rs.getString("name");
                String email = rs.getString("email");
                String department = rs.getString("department");
                String status = rs.getString("status");
                employee = new Employee(employeeId, name, email, department, status);
                return employee;
            } else {
                System.out.println("Employee not found.");
                return null;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return employee;

    }

    public List<Employee> getAllEmployees() {
        try(Connection connection = DBConnection.getConnection()) {
            PreparedStatement pstmt = connection.prepareStatement(SELECT_ALL_QUERY);
            ResultSet rs = pstmt.executeQuery();

            List<Employee> employees = new ArrayList<>();

            while(rs.next()) {
                int employeeId = rs.getInt("employee_id");
                String name = rs.getString("name");
                String email = rs.getString("email");
                String department = rs.getString("department");
                String status = rs.getString("status");
                Employee employee = new Employee(employeeId, name, email, department, status);
                employees.add(employee);
            }
            return employees;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
