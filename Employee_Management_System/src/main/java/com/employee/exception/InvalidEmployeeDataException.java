package com.employee.exception;

public class InvalidEmployeeDataException extends Exception {

    public InvalidEmployeeDataException(String message) {
        super(message);
    }
}