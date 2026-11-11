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
    void shouldProcessDocumentSuccessfully() throws Exception {

        // Arrange
        when(connectionProvider.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(statement);

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

        String documentContent = "Employee ID: 101, Name: Sharath";

        ResponseBytes<GetObjectResponse> documentBytes = ResponseBytes.fromByteArray(GetObjectResponse.builder().build(), documentContent.getBytes(StandardCharsets.UTF_8));

        doReturn(documentBytes).when(s3Client).getObject(any(GetObjectRequest.class), org.mockito.ArgumentMatchers.<ResponseTransformer<GetObjectResponse, ResponseBytes<GetObjectResponse>>>any());

        // Act
        assertDoesNotThrow(() -> handler.handleRequest(event, context));

        // Assert
        verify(s3Client, times(1)).getObject(any(GetObjectRequest.class), any(ResponseTransformer.class));

        verify(connectionProvider, times(1)).getConnection();

        verify(statement).setInt(1, 101);
        verify(statement).setString(2, "test-bucket");
        verify(statement).setString(3, "sample.txt");
        verify(statement).setString(4, "SUCCESS");
        verify(statement).setString(5, null);

        verify(statement, times(1)).executeUpdate();

        verify(connection, times(1)).close();
    }
}