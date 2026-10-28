package com.documentprocessing;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.documentprocessing.util.DBConnection;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

public class DocumentProcessingHandler implements RequestHandler<SQSEvent, Void> {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final S3Client s3Client = S3Client.create();

    @Override
    public Void handleRequest(SQSEvent event, Context context) {

        for (SQSEvent.SQSMessage message : event.getRecords()) {
            String bucketName = null;
            String objectKey = null;
            Integer employeeId = null;

            try {
                JsonNode documentInfo = objectMapper.readTree(message.getBody());

                bucketName = documentInfo.path("bucketName").asText();

                objectKey = documentInfo.path("objectKey").asText();

                JsonNode employeeIdNode = documentInfo.path("employeeId");

                if (bucketName.isBlank() || objectKey.isBlank()) {
                    throw new IllegalArgumentException(
                            "bucketName and objectKey are required");
                }

                if (!employeeIdNode.isIntegralNumber() || !employeeIdNode.canConvertToInt() || employeeIdNode.asInt() <= 0) {
                    throw new IllegalArgumentException(
                            "A valid positive employeeId is required");
                }

                employeeId = employeeIdNode.asInt();

                GetObjectRequest request = GetObjectRequest.builder().bucket(bucketName).key(objectKey).build();

                String documentContent = s3Client.getObject(request, ResponseTransformer.toBytes()).asString(StandardCharsets.UTF_8);

                context.getLogger().log("Document downloaded successfully: " + objectKey + "\n");

                context.getLogger().log("Document content:\n" + documentContent + "\n");

                saveProcessingStatus(
                        employeeId,
                        bucketName,
                        objectKey,
                        "SUCCESS",
                        null);

                context.getLogger().log(
                        "Processing status saved to PostgreSQL for employeeId: "
                        + employeeId + "\n");
            } catch (Exception exception) {
                context.getLogger().log("Document processing failed: " + exception.getMessage() + "\n");

                if (employeeId != null
                        && bucketName != null
                        && !bucketName.isBlank()
                        && objectKey != null
                        && !objectKey.isBlank()) {
                    try {
                        saveProcessingStatus(
                                employeeId,
                                bucketName,
                                objectKey,
                                "FAILED",
                                exception.getMessage());
                    } catch (Exception databaseException) {
                        context.getLogger().log("Failed to save failure status: " + databaseException.getMessage() + "\n");
                    }
                }
                throw new RuntimeException("Document processing failed", exception);
            }
        }
        return null;
    }

    private void saveProcessingStatus(int employeeId, String bucketName, String objectKey, String status, String errorMessage) throws SQLException {

        String sql = """
                INSERT INTO document_processing (employee_id, bucket_name, object_key,processing_status, error_message)
                VALUES (?, ?, ?, ?, ?)
                """;
        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, employeeId);
            statement.setString(2, bucketName);
            statement.setString(3, objectKey);
            statement.setString(4, status);
            statement.setString(5, errorMessage);

            statement.executeUpdate();
        }
    }
}
