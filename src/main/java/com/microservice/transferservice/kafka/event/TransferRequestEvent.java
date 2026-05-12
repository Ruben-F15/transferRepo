package com.microservice.transferservice.kafka.event;

import java.math.BigDecimal;

/**
 * Payload que se envía por Kafka. Contiene la solicitud de pago.
 * Este DTO es el "contrato" de lo que se va a intentar en la transferencia.
 */
public record TransferRequestEvent(
        String sourceAccountId, 
        String destinationAccountId, 
        BigDecimal amount,
        Long correlationId // Se añade un ID para rastrear la saga// mejor añadirlo en MDC - HEADER KAFKA????
) {}
