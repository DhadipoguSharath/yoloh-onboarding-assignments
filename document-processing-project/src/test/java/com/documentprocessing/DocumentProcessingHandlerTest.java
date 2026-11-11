package com.documentprocessing;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.Mock;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;

import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

@ExtendWith(MockitoExtension.class)
class DocumentProcessingHandlerTest {

    @Mock
    private S3Client s3Client;

    @Mock
    private DocumentProcessingHandler.ConnectionProvider connectionProvider;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement statement;

    @Mock
    private Context context;

    @Mock
    private LambdaLogger logger;

    private DocumentProcessingHandler handler;

    @BeforeEach
    void setUp() throws Exception {
        handler = new DocumentProcessingHandler(s3Client, connectionProvider);

        when(context.getLogger()).thenReturn(logger);

    }

    @Test
    void shouldThrowExceptionForInvalidSqsMessage() {

        // Arrange
        SQSEvent.SQSMessage message = new SQSEvent.SQSMessage();
        message.setBody("invalid-json");

        SQSEvent event = new SQSEvent();
        event.setRecords(List.of(message));

        // Act & Assert
        assertThrows(
                RuntimeException.class,
                () -> handler.handleRequest(event, context)
        );

        verify(s3Client, times(0)).getObject(
                any(GetObjectRequest.class),
                any(ResponseTransformer.class)
        );
    }

    @Test
    void shouldThrowExceptionWhenBucketNameIsMissing() {

        String messageBody = """
            {
                "bucketName": "",
                "objectKey": "sample.txt",
                "employeeId": 101
            }
            """;

        SQSEvent.SQSMessage message = new SQSEvent.SQSMessage();
        message.setBody(messageBody);

        SQSEvent event = new SQSEvent();
        event.setRecords(List.of(message));

        assertThrows(
                RuntimeException.class,
                () -> handler.handleRequest(event, context)
        );

        verify(s3Client, times(0)).getObject(
                any(GetObjectRequest.class),
                any(ResponseTransformer.class)
        );
    }

    @Test
    void shouldThrowExceptionWhenObjectKeyIsMissing() {

        String messageBody = """
            {
                "bucketName": "test-bucket",
                "objectKey": "",
                "employeeId": 101
            }
            """;

        SQSEvent.SQSMessage message = new SQSEvent.SQSMessage();
        message.setBody(messageBody);

        SQSEvent event = new SQSEvent();
        event.setRecords(List.of(message));

        assertThrows(
                RuntimeException.class,
                () -> handler.handleRequest(event, context)
        );

        verify(s3Client, times(0)).getObject(
                any(GetObjectRequest.class),
                any(ResponseTransformer.class)
        );
    }

    @Test
    void shouldThrowExceptionWhenEmployeeIdIsInvalid() {

        String messageBody = """
            {
                "bucketName": "test-bucket",
                "objectKey": "sample.txt",
                "employeeId": -1
            }
            """;

        SQSEvent.SQSMessage message = new SQSEvent.SQSMessage();
        message.setBody(messageBody);

        SQSEvent event = new SQSEvent();
        event.setRecords(List.of(message));

        assertThrows(
                RuntimeException.class,
                () -> handler.handleRequest(event, context)
        );

        verify(s3Client, times(0)).getObject(
                any(GetObjectRequest.class),
                any(ResponseTransformer.class)
        );
    }

    @Test
    void shouldThrowExceptionWhenS3DownloadFails() throws Exception {

        String messageBody = """
            {
                "bucketName": "test-bucket",
                "objectKey": "sample.txt",
                "employeeId": 101
            }
            """;

        SQSEvent.SQSMessage message = new SQSEvent.SQSMessage();
        message.setBody(messageBody);

        SQSEvent event = new SQSEvent();
        event.setRecords(List.of(message));

        when(connectionProvider.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(statement);

        doThrow(S3Exception.builder()
                .message("S3 download failed")
                .build())
                .when(s3Client)
                .getObject(
                        any(GetObjectRequest.class),
                        org.mockito.ArgumentMatchers
                                .<ResponseTransformer<software.amazon.awssdk.services.s3.model.GetObjectResponse, ResponseBytes<software.amazon.awssdk.services.s3.model.GetObjectResponse>>>any()
                );

        assertThrows(
                RuntimeException.class,
                () -> handler.handleRequest(event, context)
        );

        verify(s3Client, times(1)).getObject(
                any(GetObjectRequest.class),
                any(ResponseTransformer.class)
        );

        verify(statement).setString(4, "FAILED");
    }

    @Test
    void shouldThrowExceptionWhenDatabaseUpdateFails() throws Exception {

        String messageBody = """
            {
                "bucketName": "test-bucket",
                "objectKey": "sample.txt",
                "employeeId": 101
            }
            """;

        SQSEvent.SQSMessage message = new SQSEvent.SQSMessage();
        message.setBody(messageBody);

        SQSEvent event = new SQSEvent();
        event.setRecords(List.of(message));

        when(connectionProvider.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(statement);

        doThrow(new SQLException("Database update failed"))
                .when(statement)
                .executeUpdate();

        ResponseBytes<GetObjectResponse> documentBytes
                = ResponseBytes.fromByteArray(
                        GetObjectResponse.builder().build(),
                        "Test document".getBytes(StandardCharsets.UTF_8)
                );

        doReturn(documentBytes).when(s3Client).getObject(
                any(GetObjectRequest.class),
                org.mockito.ArgumentMatchers
                        .<ResponseTransformer<GetObjectResponse, ResponseBytes<GetObjectResponse>>>any()
        );

        assertThrows(
                RuntimeException.class,
                () -> handler.handleRequest(event, context)
        );

        verify(statement, times(2)).executeUpdate();
        verify(statement).setString(4, "SUCCESS");
        verify(statement).setString(4, "FAILED");
    }
}