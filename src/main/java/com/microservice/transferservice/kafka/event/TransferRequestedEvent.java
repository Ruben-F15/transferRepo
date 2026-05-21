package com.microservice.transferservice.kafka.event;

import lombok.Builder;

import java.math.BigDecimal;

/**
 * Payload que se envía por Kafka. Contiene la info para reservar fondos.
 *
 */
@Builder
public record TransferRequestedEvent(
        String sourceUserId,
        BigDecimal amount,
        String transactionId // Se añade un ID para rastrear la saga// mejor añadirlo en MDC - HEADER KAFKA????
) {}
