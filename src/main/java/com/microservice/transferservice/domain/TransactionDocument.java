package com.microservice.transferservice.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Builder
@Entity
@Table(name = "transfer_transactions")
@AllArgsConstructor @NoArgsConstructor
public class TransactionDocument {

    @Id // Usamos UUID como la clave primaria lógica y física (String)
    @Column(name = "id", length = 36, unique = true, nullable = false)
    private String id;

    // La referencia de negocio para que podamos buscar un caso específico
    @Column(name = "referenceNumber", nullable = false, unique = true)
    private String referenceNumber;

    // Claves foráneas: usamos String para mantener la interoperabilidad con Mongo
    @Column(name = "source_account_id", nullable = false)
    private String source_account_id; //(Foreign Key).

    @Column(name = "destination_account_id", nullable = false)
    private String destination_account_id; //(Foreign Key).

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "currency", nullable = false)
    private String currency;

    @Column(name = "transactionType", nullable = false)
    private TransactionType transactionType; //(Enum: CREDIT, DEBIT, ADJUSTMENT).

    @Column(name = "status", nullable = false)
    private TransactionStatus status; //(Enum: PENDIENTE, COMPLETED, FAILED).

    // Campos CRÍTICOS de Auditoría
    @Column(name = "source_expected_balance", nullable = false)
    private BigDecimal sourceExpectedBalance; // Balance DEBE pasar por RESTORABLE.

    @Column(name = "destination_expected_balance", nullable = false)
    private BigDecimal destinationExpectedBalance; // Balance DEBE pasar por RESTORABLE.

    // Timestamps
    @Column(name = "transaction_start_date", nullable = false)
    private Instant transaction_start_date;

    @Column(name = "status_updated_at", nullable = false)
    private Instant status_updated_at;
}
