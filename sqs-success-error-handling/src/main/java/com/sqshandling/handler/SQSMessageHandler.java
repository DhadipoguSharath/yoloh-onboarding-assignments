package com.sqshandling.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.sqshandling.config.SQSClientProvider;
import com.sqshandling.publisher.SQSMessagePublisher;
import com.sqshandling.service.MessageProcessingService;

public class SQSMessageHandler implements RequestHandler<SQSEvent, Void> {

    private final MessageProcessingService processingService;

    public SQSMessageHandler() {

        String successQueueUrl = System.getenv("SUCCESS_QUEUE_URL");
        String errorQueueUrl = System.getenv("ERROR_QUEUE_URL");

        SQSMessagePublisher publisher
                = new SQSMessagePublisher(SQSClientProvider.getSqsClient());

        this.processingService = new MessageProcessingService(
                publisher,
                successQueueUrl,
                errorQueueUrl
        );
    }

    @Override
    public Void handleRequest(SQSEvent event, Context context) {

        for (SQSEvent.SQSMessage message : event.getRecords()) {

            try {
                processingService.processMessage(message.getBody());

            } catch (Exception exception) {

                context.getLogger().log(
                        "Message processing failed: " + exception.getMessage()
                );

                throw new RuntimeException(
                        "Failed to process or publish SQS message", exception
                );
            }
        }

        return null;
    }
}
