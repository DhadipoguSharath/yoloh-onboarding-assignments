package com.sqshandling.config;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;

public class SQSClientProvider {

    private static final SqsClient SQS_CLIENT = SqsClient.builder()
            .region(Region.of(System.getenv("AWS_REGION")))
            .build();

    public static SqsClient getSqsClient() {
        return SQS_CLIENT;
    }
}

