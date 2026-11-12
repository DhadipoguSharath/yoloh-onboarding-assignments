package com.employee;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

import com.employee.exception.EmployeeNotFoundException;
import com.employee.exception.InvalidEmployeeDataException;
import com.employee.model.Employee;
import com.employee.model.EmployeeStatus;
import com.employee.service.EmployeeService;

public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        EmployeeService employeeService = new EmployeeService();

        int choice;

        System.out.println("==================================");
        System.out.println(" Welcome to Employee Management ");
        System.out.println("==================================");

        do {
            System.out.println("\nPlease select an option:");
            System.out.println("1. Add Employee");
            System.out.println("2. View Employee by ID");
            System.out.println("3. View All Employees");
            System.out.println("4. Update Employee");
            System.out.println("5. Delete Employee");
            System.out.println("6. Exit");

            choice = readInteger(scanner, "Enter your choice: ");

            try {
                switch (choice) {

                    case 1:
                        System.out.println("\n--- Add Employee ---");

                        System.out.print("Enter name: ");
                        String name = scanner.nextLine();

                        System.out.print("Enter email: ");
                        String email = scanner.nextLine();

                        System.out.print("Enter department: ");
                        String department = scanner.nextLine();

                        EmployeeStatus status =
                                readEmployeeStatus(scanner);

                        Employee employee = new Employee(
                                name, email, department, status);

                        employeeService.addEmployee(employee);

                        System.out.println("Employee added successfully.");
                        break;

                    case 2:
                        System.out.println("\n--- View Employee ---");

                        int employeeId = readInteger(
                                scanner, "Enter employee ID: ");

                        Employee emp =
                                employeeService.getEmployeeById(employeeId);

                        System.out.println("\nEmployee Details");
                        System.out.println("ID: " + emp.getEmployeeId());
                        System.out.println("Name: " + emp.getName());
                        System.out.println("Email: " + emp.getEmail());
                        System.out.println("Department: " + emp.getDepartment());
                        System.out.println("Status: " + emp.getStatus());
                        break;

                    case 3:
                        System.out.println("\n--- All Employees ---");

                        List<Employee> employees =
                                employeeService.getAllEmployees();

                        if (employees.isEmpty()) {
                            System.out.println("No employees found.");
                        } else {
                            for (Employee e : employees) {
                                System.out.println("------------------------");
                                System.out.println("ID: " + e.getEmployeeId());
                                System.out.println("Name: " + e.getName());
                                System.out.println("Email: " + e.getEmail());
                                System.out.println(
                                        "Department: " + e.getDepartment());
                                System.out.println("Status: " + e.getStatus());
                            }
                        }
                        break;

                    case 4:
                        System.out.println("\n--- Update Employee ---");

                        int updateId = readInteger(
                                scanner, "Enter employee ID to update: ");

                        System.out.print("Enter new name: ");
                        String newName = scanner.nextLine();

                        System.out.print("Enter new email: ");
                        String newEmail = scanner.nextLine();

                        System.out.print("Enter new department: ");
                        String newDepartment = scanner.nextLine();

                        EmployeeStatus newStatus =
                                readEmployeeStatus(scanner);

                        Employee updatedEmployee = new Employee(
                                updateId,
                                newName,
                                newEmail,
                                newDepartment,
                                newStatus
                        );

                        employeeService.updateEmployee(updatedEmployee);

                        System.out.println("Employee updated successfully.");
                        break;

                    case 5:
                        System.out.println("\n--- Delete Employee ---");

                        int deleteId = readInteger(
                                scanner, "Enter employee ID to delete: ");

                        employeeService.deleteEmployee(deleteId);

                        System.out.println("Employee deleted successfully.");
                        break;

                    case 6:
                        System.out.println(
                                "Exiting Employee Management System. Goodbye!");
                        break;

                    default:
                        System.out.println(
                                "Invalid choice! Please select 1 to 6.");
                }

            } catch (EmployeeNotFoundException e) {
                System.out.println("Operation failed: " + e.getMessage());

            } catch (InvalidEmployeeDataException e) {
                System.out.println("Invalid employee data: " + e.getMessage());

            } catch (SQLException e) {
                System.out.println(
                        "Database operation failed. Please try again.");

            } catch (IllegalArgumentException e) {
                System.out.println("Invalid input: " + e.getMessage());
            }

        } while (choice != 6);

        scanner.close();
    }

    // Reusable helper for reading integer input.
    private static int readInteger(Scanner scanner, String prompt) {

        while (true) {
            System.out.print(prompt);

            if (scanner.hasNextInt()) {
                int value = scanner.nextInt();
                scanner.nextLine();
                return value;
            }

            System.out.println("Invalid input! Please enter a number.");
            scanner.nextLine();
        }
    }

    // Reads and validates employee status using the enum.
    private static EmployeeStatus readEmployeeStatus(Scanner scanner) {

        while (true) {
            System.out.print("Enter status (ACTIVE/INACTIVE): ");

            String input = scanner.nextLine().trim().toUpperCase();

            try {
                return EmployeeStatus.valueOf(input);
            } catch (IllegalArgumentException e) {
                System.out.println(
                        "Invalid status! Enter ACTIVE or INACTIVE.");
            }
        }
    }
}