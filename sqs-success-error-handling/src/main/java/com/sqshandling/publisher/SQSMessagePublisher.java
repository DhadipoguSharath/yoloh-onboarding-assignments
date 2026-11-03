package com.sqshandling.publisher;

import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

public class SQSMessagePublisher {
    private final SqsClient sqsClient;

    public SQSMessagePublisher(SqsClient sqsClient) {
        this.sqsClient = sqsClient;
    }

    public void publishMessage(String queueUrl, String messageBody) {

        SendMessageRequest request = SendMessageRequest.builder().queueUrl(queueUrl).messageBody(messageBody).build();

        SendMessageResponse response = sqsClient.sendMessage(request);

        System.out.println("Message published successfully. Message ID: "+ response.messageId());
    }
}