package com.microservice.transferservice.kafka.event;

public record FundsDebitFailedEvent(
        String transactionId, // Se añade un ID para rastrear la saga// mejor añadirlo en MDC - HEADER KAFKA????
        String failReason,
        String sourceUserId
) {
}
