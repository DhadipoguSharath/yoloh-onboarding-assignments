package com.employee.model;

public class Employee {

    private int employeeId;
    private String name;
    private String email;
    private String department;
    private EmployeeStatus status;

    public Employee(int employeeId, String name, String email,
            String department, EmployeeStatus status) {
        this.employeeId = employeeId;
        this.name = name;
        this.email = email;
        this.department = department;
        this.status = status;
    }

    public Employee(String name, String email,
            String department, EmployeeStatus status) {
        this.name = name;
        this.email = email;
        this.department = department;
        this.status = status;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public EmployeeStatus getStatus() {
        return status;
    }

    public void setStatus(EmployeeStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "employeeId=" + employeeId
                + ", name='" + name + '\''
                + ", email='" + email + '\''
                + ", department='" + department + '\''
                + ", status='" + status + '\'';
    }
}
