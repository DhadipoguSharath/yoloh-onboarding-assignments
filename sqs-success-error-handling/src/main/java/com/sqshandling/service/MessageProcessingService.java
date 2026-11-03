package com.sqshandling.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sqshandling.publisher.SQSMessagePublisher;

public class MessageProcessingService {

    private final SQSMessagePublisher publisher;
    private final String successQueueUrl;
    private final String errorQueueUrl;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public MessageProcessingService(
            SQSMessagePublisher publisher,
            String successQueueUrl,
            String errorQueueUrl) {

        this.publisher = publisher;
        this.successQueueUrl = successQueueUrl;
        this.errorQueueUrl = errorQueueUrl;
    }

    public void processMessage(String messageBody) throws Exception {

        ObjectNode response = objectMapper.createObjectNode();
        boolean success;

        try {
            JsonNode request = objectMapper.readTree(messageBody);

            JsonNode employeeId = request.get("employeeId");
            JsonNode employeeName = request.get("employeeName");

            if (employeeId == null || employeeId.isNull()) {
                throw new IllegalArgumentException("Employee ID is required");
            }

            if (employeeName == null || employeeName.asText().isBlank()) {
                throw new IllegalArgumentException("Employee name is required");
            }

            response.set("request", request);
            response.put("status", "SUCCESS");
            response.put("result", "Employee request processed successfully");

            success = true;

        } catch (Exception exception) {

            try {
                response.set("request", objectMapper.readTree(messageBody));
            } catch (Exception parsingException) {
                response.put("request", messageBody);
            }
            response.put("status", "ERROR");
            response.put("error", exception.getMessage());

            success = false;
        }

        String responseMessage = objectMapper.writeValueAsString(response);

        if (success) {
            publisher.publishMessage(successQueueUrl, responseMessage);
        } else {
            publisher.publishMessage(errorQueueUrl, responseMessage);
        }
    }
}
