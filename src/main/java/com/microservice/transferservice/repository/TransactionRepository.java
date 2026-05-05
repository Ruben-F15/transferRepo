package com.microservice.transferservice.repository;

import com.microservice.transferservice.domain.TransactionDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;

public interface TransactionRepository extends JpaRepository<TransactionDocument, String> {

    /**
     * 1. Method de entrada síncrona (Webhook/REST/API Gateway call).
     * Inicia la Saga: Crea el TransactionDocument en estado PENDING y emite el primer evento a Kafka.
     * @param sourceId Cuenta emisora.
     * @param destinationId Cuenta receptora.
     * @param amount Monto.
     * @return El UUID de la saga iniciada.
     */
    String initiateTransfer(String sourceId, String destinationId, BigDecimal amount);


}
