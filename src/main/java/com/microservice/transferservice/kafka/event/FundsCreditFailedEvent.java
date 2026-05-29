package com.microservice.transferservice.kafka.event;

public record FundsCreditFailedEvent(
        String transactionId,
        String failReason,
        String sourceUserId
) {}
