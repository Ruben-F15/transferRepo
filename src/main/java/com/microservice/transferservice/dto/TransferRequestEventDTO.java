package com.microservice.transferservice.dto;

import java.math.BigDecimal;

/**
 * Payload que se envía por Kafka. Contiene la solicitud de pago.
 * Este DTO es el "contrato" de lo que se va a intentar en la transferencia.
 */
public record TransferRequestEventDTO(
        String sourceAccountId, 
        String destinationAccountId, 
        BigDecimal amount,
        Long correlationId // Se añade un ID para rastrear la saga
) {}
