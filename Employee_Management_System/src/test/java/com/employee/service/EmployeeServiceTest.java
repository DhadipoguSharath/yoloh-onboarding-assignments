package com.employee.service;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.employee.dao.EmployeeDAO;
import com.employee.exception.EmployeeNotFoundException;
import com.employee.exception.InvalidEmployeeDataException;
import com.employee.model.Employee;
import com.employee.model.EmployeeStatus;

class EmployeeServiceTest {

    private EmployeeDAO employeeDAO;
    private EmployeeService employeeService;
    private Employee employee;

    @BeforeEach
    void setUp() {
        employeeDAO = mock(EmployeeDAO.class);
        employeeService = new EmployeeService(employeeDAO);

        employee = new Employee(
                1, "Sharath", "sharath@gmail.com",
                "Engineering", EmployeeStatus.ACTIVE
        );
    }

    // ADD EMPLOYEE TESTS
    @Test
    void shouldAddEmployeeSuccessfully() throws Exception {
        employeeService.addEmployee(employee);
        verify(employeeDAO).addEmployee(employee);
    }

    @Test
    void shouldThrowExceptionWhenAddingNullEmployee() {
        assertThrows(InvalidEmployeeDataException.class,() -> employeeService.addEmployee(null));
        verifyNoInteractions(employeeDAO);
    }

    @Test
    void shouldThrowExceptionWhenEmployeeNameIsEmpty() {
        employee.setName(" ");
        assertThrows(InvalidEmployeeDataException.class,() -> employeeService.addEmployee(employee));
        verifyNoInteractions(employeeDAO);
    }

    @Test
    void shouldThrowExceptionWhenEmployeeEmailIsEmpty() {
        employee.setEmail("");
        assertThrows(InvalidEmployeeDataException.class,() -> employeeService.addEmployee(employee));
        verifyNoInteractions(employeeDAO);
    }

    @Test
    void shouldThrowExceptionWhenEmployeeEmailIsInvalid() {
        employee.setEmail("invalid-email");
        assertThrows(InvalidEmployeeDataException.class,() -> employeeService.addEmployee(employee));
        verifyNoInteractions(employeeDAO);
    }

    @Test
    void shouldThrowExceptionWhenEmployeeDepartmentIsEmpty() {
        employee.setDepartment(" ");
        assertThrows(InvalidEmployeeDataException.class,() -> employeeService.addEmployee(employee));
        verifyNoInteractions(employeeDAO);
    }

    @Test
    void shouldThrowExceptionWhenEmployeeStatusIsNull() {
        employee.setStatus(null);
        assertThrows(InvalidEmployeeDataException.class,() -> employeeService.addEmployee(employee));
        verifyNoInteractions(employeeDAO);
    }

    @Test
    void shouldPropagateSQLExceptionWhenAddingEmployee() throws Exception {
        doThrow(new SQLException("Database error")).when(employeeDAO).addEmployee(employee);
        assertThrows(SQLException.class,() -> employeeService.addEmployee(employee));
    }

    // UPDATE EMPLOYEE TESTS
    @Test
    void shouldUpdateEmployeeSuccessfully() throws Exception {
        employeeService.updateEmployee(employee);
        verify(employeeDAO).updateEmployee(employee);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNullEmployee() {
        assertThrows(InvalidEmployeeDataException.class,() -> employeeService.updateEmployee(null));
        verifyNoInteractions(employeeDAO);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingEmployeeWithInvalidId() {
        employee.setEmployeeId(0);
        assertThrows(InvalidEmployeeDataException.class,() -> employeeService.updateEmployee(employee));
        verifyNoInteractions(employeeDAO);
    }

    @Test
    void shouldPropagateEmployeeNotFoundExceptionDuringUpdate() throws Exception {
        doThrow(new EmployeeNotFoundException("Employee not found")).when(employeeDAO).updateEmployee(employee);
        assertThrows(EmployeeNotFoundException.class,() -> employeeService.updateEmployee(employee));
    }

    @Test
    void shouldPropagateSQLExceptionDuringUpdate() throws Exception {

        doThrow(new SQLException("Database error")).when(employeeDAO).updateEmployee(employee);

        assertThrows(SQLException.class,() -> employeeService.updateEmployee(employee));
    }

    // DELETE EMPLOYEE TESTS
    @Test
    void shouldDeleteEmployeeSuccessfully() throws Exception {
        employeeService.deleteEmployee(1);
        verify(employeeDAO).deleteEmployee(1);
    }

    @Test
    void shouldThrowExceptionWhenDeletingEmployeeWithInvalidId() {
        assertThrows(IllegalArgumentException.class,() -> employeeService.deleteEmployee(0));
        verifyNoInteractions(employeeDAO);
    }

    @Test
    void shouldPropagateEmployeeNotFoundExceptionDuringDelete() throws Exception {
        doThrow(new EmployeeNotFoundException("Employee not found")).when(employeeDAO).deleteEmployee(1);
        assertThrows(EmployeeNotFoundException.class,() -> employeeService.deleteEmployee(1));
    }

    @Test
    void shouldPropagateSQLExceptionDuringDelete() throws Exception {
        doThrow(new SQLException("Database error")).when(employeeDAO).deleteEmployee(1);
        assertThrows(SQLException.class,() -> employeeService.deleteEmployee(1));
    }

    // GET EMPLOYEE BY ID TESTS
    @Test
    void shouldReturnEmployeeWhenFound() throws Exception {
        when(employeeDAO.getEmployeeById(1)).thenReturn(employee);
        Employee result = employeeService.getEmployeeById(1);
        assertNotNull(result);
        assertEquals(employee, result);
        verify(employeeDAO).getEmployeeById(1);
    }

    @Test
    void shouldThrowExceptionWhenSearchingWithInvalidId() {
        assertThrows(IllegalArgumentException.class,() -> employeeService.getEmployeeById(0));
        verifyNoInteractions(employeeDAO);
    }

    @Test
    void shouldPropagateEmployeeNotFoundExceptionDuringSearch() throws Exception {
        when(employeeDAO.getEmployeeById(1)).thenThrow(new EmployeeNotFoundException("Employee not found"));
        assertThrows(EmployeeNotFoundException.class,() -> employeeService.getEmployeeById(1));
    }

    @Test
    void shouldPropagateSQLExceptionDuringSearch() throws Exception {
        when(employeeDAO.getEmployeeById(1)).thenThrow(new SQLException("Database error"));
        assertThrows(SQLException.class,() -> employeeService.getEmployeeById(1));
    }

    // GET ALL EMPLOYEES TEST
    @Test
    void shouldReturnAllEmployees() throws Exception {
        List<Employee> employees = List.of(employee,new Employee(2, "John", "john@gmail.com","Testing", EmployeeStatus.ACTIVE));
        when(employeeDAO.getAllEmployees()).thenReturn(employees);
        List<Employee> result = employeeService.getAllEmployees();
        assertEquals(2, result.size());
        assertEquals(employees, result);
        verify(employeeDAO).getAllEmployees();
    }

    @Test
    void shouldReturnEmptyListWhenNoEmployeesExist() throws Exception {
        when(employeeDAO.getAllEmployees()).thenReturn(List.of());
        List<Employee> result = employeeService.getAllEmployees();
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldPropagateSQLExceptionWhenGettingAllEmployees() throws Exception {
        when(employeeDAO.getAllEmployees()).thenThrow(new SQLException("Database error"));
        assertThrows(SQLException.class,() -> employeeService.getAllEmployees());
    }
}