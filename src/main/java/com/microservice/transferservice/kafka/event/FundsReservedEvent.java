package com.microservice.transferservice.kafka.event;

public record FundsReservedEvent(
        String sourceUserId,
        String transactionId // Se añade un ID para rastrear la saga// mejor añadirlo en MDC - HEADER KAFKA????
) {}
