package com.microservice.transferservice.kafka.event;

import lombok.Builder;

import java.math.BigDecimal;

/**
 * Payload que se envía por Kafka. Contiene la solicitud de pago.
 * Este DTO es el "contrato" de lo que se va a intentar en la transferencia.
 */
@Builder
public record TransferRequestedEvent(
        String sourceUserId,
        String destinationUserId,
        BigDecimal amount,
        String transactionId // Se añade un ID para rastrear la saga// mejor añadirlo en MDC - HEADER KAFKA????
) {}
