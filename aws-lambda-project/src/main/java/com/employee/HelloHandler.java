package com.employee;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.employee.dao.PostgresEmployeeDAO;
import com.employee.model.Employee;
import com.employee.service.EmployeeService;
import com.fasterxml.jackson.databind.ObjectMapper;

public class HelloHandler implements
        RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> {

    private final EmployeeService employeeService;
    private final ObjectMapper objectMapper;

    public HelloHandler() {
        this.employeeService = new EmployeeService(
                new PostgresEmployeeDAO()
        );
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public APIGatewayV2HTTPResponse handleRequest(
            APIGatewayV2HTTPEvent event, Context context) {

        try {
            // Safety check for empty or invalid event context
            if (event == null || event.getRequestContext() == null || event.getRequestContext().getHttp() == null) {
                return response(400, Map.of("message", "Invalid request context"));
            }

            String method = event.getRequestContext().getHttp().getMethod();
            String path = event.getRawPath();

            context.getLogger().log("Received " + method + " " + path + "\n");

            // 1. CREATE EMPLOYEE (POST /employees or /employees/)
            if ("POST".equalsIgnoreCase(method) && path.matches("/employees/?")) {

                String body = event.getBody();

                if (body == null || body.isBlank()) {
                    return response(400, "Request body cannot be empty");
                }

                Employee employee;

                try {
                    employee = objectMapper.readValue(body, Employee.class);
                } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
                    return response(400, "Invalid JSON request body");
                }

                Employee created = employeeService.addEmployee(employee);
                return response(201, created);
            }

            // 2. GET ALL EMPLOYEES (GET /employees or /employees/)
            if ("GET".equalsIgnoreCase(method) && path.matches("/employees/?")) {
                List<Employee> employees = employeeService.getAllEmployees();
                return response(200, employees);
            }

            // 3. GET EMPLOYEE BY ID (GET /employees/{id})
            if ("GET".equalsIgnoreCase(method) && path.matches("/employees/\\d+/?")) {
                int employeeId = extractIdFromPath(path);
                Employee employee = employeeService.getEmployeeById(employeeId);

                if (employee != null) {
                    return response(200, employee);
                } else {
                    return response(404, Map.of("message", "Employee not found with ID: " + employeeId));
                }
            }

            // 4. UPDATE EMPLOYEE (PUT /employees/{id})
            if ("PUT".equalsIgnoreCase(method) && path.matches("/employees/\\d+/?")) {
                int employeeId = extractIdFromPath(path);

                Employee employee = objectMapper.readValue(event.getBody(), Employee.class);
                employee.setEmployeeId(employeeId); // Inject path parameter ID into model

                Employee updated = employeeService.updateEmployee(employee);
                if (updated != null) {
                    return response(200, updated);
                } else {
                    return response(404, Map.of("message", "Employee not found to update with ID: " + employeeId));
                }
            }

            // 5. DELETE EMPLOYEE (DELETE /employees/{id})
            if ("DELETE".equalsIgnoreCase(method) && path.matches("/employees/\\d+/?")) {
                int employeeId = extractIdFromPath(path);
                boolean deleted = employeeService.deleteEmployee(employeeId);

                if (deleted) {
                    return response(200, Map.of(
                            "message", "Employee deleted successfully",
                            "employeeId", employeeId
                    ));
                } else {
                    return response(404, Map.of("message", "Employee not found to delete with ID: " + employeeId));
                }
            }

            // UNMATCHED ROUTE
            return response(404, Map.of("message", "Route not found"));

        } catch (IllegalArgumentException e) {
            return response(400, Map.of("message", e.getMessage()));

        } catch (Exception e) {
            Throwable cause = e;
            while (cause.getCause() != null) {
                cause = cause.getCause();
            }

            context.getLogger().log(
                    "Request failed: " + e.getMessage()
                    + "; Root cause: " + cause.getClass().getSimpleName()
                    + ": " + cause.getMessage() + "\n"
            );

            return response(500, Map.of("message", "Internal server error"));
        }
    }

    private int extractIdFromPath(String path) {
        String idStr = path.replaceAll("[^0-9]", "");
        return Integer.parseInt(idStr);
    }

    private APIGatewayV2HTTPResponse response(int statusCode, Object body) {
        try {
            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json");

            return APIGatewayV2HTTPResponse.builder()
                    .withStatusCode(statusCode)
                    .withHeaders(headers)
                    .withBody(objectMapper.writeValueAsString(body))
                    .build();

        } catch (Exception e) {
            return APIGatewayV2HTTPResponse.builder()
                    .withStatusCode(500)
                    .withBody("{\"message\":\"Failed to build response\"}")
                    .build();
        }
    }
}
