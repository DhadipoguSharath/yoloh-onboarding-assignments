
package com.employee;

import com.employee.model.Employee;
import com.employee.service.EmployeeService;
import java.util.List;
import java.util.Scanner;

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
            System.out.print("Enter your choice: ");

            while (!scanner.hasNextInt()) {
                System.out.println("Invalid input! Please enter a number.");
                scanner.nextLine();
                System.out.print("Enter your choice: ");
            }

            choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline

            switch (choice) {

                case 1:
                    System.out.println("\n--- Add Employee ---");

                    System.out.print("Enter name: ");
                    String name = scanner.nextLine();

                    System.out.print("Enter email: ");
                    String email = scanner.nextLine();

                    System.out.print("Enter department: ");
                    String department = scanner.nextLine();

                    System.out.print("Enter status (ACTIVE/INACTIVE): ");
                    String status = scanner.nextLine();

                    Employee employee =
                            new Employee(name, email, department, status);

                    employeeService.addEmployee(employee);
                    break;

                case 2:
                    System.out.println("\n--- View Employee ---");

                    System.out.print("Enter employee ID: ");

                    while (!scanner.hasNextInt()) {
                        System.out.println("Invalid ID! Enter a number.");
                        scanner.nextLine();
                        System.out.print("Enter employee ID: ");
                    }

                    int employeeId = scanner.nextInt();
                    scanner.nextLine();

                    Employee emp =
                            employeeService.getEmployeeById(employeeId);

                    if (emp != null) {
                        System.out.println("\nEmployee Details");
                        System.out.println("ID: " + emp.getEmployeeId());
                        System.out.println("Name: " + emp.getName());
                        System.out.println("Email: " + emp.getEmail());
                        System.out.println("Department: " + emp.getDepartment());
                        System.out.println("Status: " + emp.getStatus());
                    } else {
                        System.out.println("Employee not found.");
                    }
                    break;

                case 3:
                    System.out.println("\n--- All Employees ---");

                    List<Employee> employees =
                            employeeService.getAllEmployees();

                    if (employees == null || employees.isEmpty()) {
                        System.out.println("No employees found.");
                    } else {
                        for (Employee e : employees) {
                            System.out.println("------------------------");
                            System.out.println("ID: " + e.getEmployeeId());
                            System.out.println("Name: " + e.getName());
                            System.out.println("Email: " + e.getEmail());
                            System.out.println("Department: " + e.getDepartment());
                            System.out.println("Status: " + e.getStatus());
                        }
                    }
                    break;

                case 4:
                    System.out.println("\n--- Update Employee ---");

                    System.out.print("Enter employee ID to update: ");

                    while (!scanner.hasNextInt()) {
                        System.out.println("Invalid ID! Enter a number.");
                        scanner.nextLine();
                        System.out.print("Enter employee ID to update: ");
                    }

                    int updateId = scanner.nextInt();
                    scanner.nextLine();

                    System.out.print("Enter new name: ");
                    String newName = scanner.nextLine();

                    System.out.print("Enter new email: ");
                    String newEmail = scanner.nextLine();

                    System.out.print("Enter new department: ");
                    String newDepartment = scanner.nextLine();

                    System.out.print("Enter new status (ACTIVE/INACTIVE): ");
                    String newStatus = scanner.nextLine();

                    Employee updatedEmployee = new Employee(
                            updateId,
                            newName,
                            newEmail,
                            newDepartment,
                            newStatus
                    );

                    employeeService.updateEmployee(updatedEmployee);
                    break;

                case 5:
                    System.out.println("\n--- Delete Employee ---");

                    System.out.print("Enter employee ID to delete: ");

                    while (!scanner.hasNextInt()) {
                        System.out.println("Invalid ID! Enter a number.");
                        scanner.nextLine();
                        System.out.print("Enter employee ID to delete: ");
                    }

                    int deleteId = scanner.nextInt();
                    scanner.nextLine();

                    employeeService.deleteEmployee(deleteId);
                    break;

                case 6:
                    System.out.println("Exiting Employee Management System. Goodbye!");
                    break;

                default:
                    System.out.println("Invalid choice! Please select 1 to 6.");
            }

        } while (choice != 6);

        scanner.close();
    }
}
