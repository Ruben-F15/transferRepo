package com.microservice.transferservice.domain.model;

import com.microservice.transferservice.domain.enums.TransferStatus;
import com.microservice.transferservice.domain.enums.TransferType;

import lombok.*;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Builder
@Document(collection = "transactions")
@AllArgsConstructor @NoArgsConstructor
public class TransferDocument {

    @Id
    private String id; // UUID

    // Claves foráneas: usamos String para mantener la interoperabilidad con Mongo
    private String sourceAccountId; //(Foreign Key).

    private String destinationAccountId; //(Foreign Key).

    private BigDecimal amount;

    private String currency; //Por ahora siempre EUR

    private TransferType transferType;

    private TransferStatus status; //(Enum: PENDIENTE, COMPLETED, FAILED).

    // Timestamps
    private Instant createdAt;

    private Instant statusUpdatedAt;

    private String failureReason;
}
