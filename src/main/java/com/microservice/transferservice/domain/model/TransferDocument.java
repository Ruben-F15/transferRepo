package com.microservice.transferservice.domain.model;

import com.microservice.transferservice.domain.enums.TransferStatus;
import com.microservice.transferservice.domain.enums.TransferType;

import lombok.*;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;

@Getter
@Setter
@Builder
@Document(collection = "transactions")
@AllArgsConstructor
@NoArgsConstructor
public class TransferDocument {

    @Id
    private ObjectId id;

    // Claves foráneas: usamos String para mantener la interoperabilidad con Mongo
    @Indexed
    private String sourceUserId; //(Foreign Key).

    @Indexed
    private String destinationUserId; //(Foreign Key).

    private BigDecimal amount;

    private String currency; //Por ahora siempre EUR

    private TransferType transferType; // (Enum: CREDIT, DEBIT, ADJUSTMENT, INTERNAL_TRANSFER)

    @Indexed
    private TransferStatus status; //(Enum: PENDING, COMPLETED, FAILED, ROLLED_BACK)

    // Timestamps
    @Indexed
    private Instant createdAt;

    private Instant statusUpdatedAt;

    private String failureReason;

    @Indexed(unique = true)
    private String transactionId; // UUID

    public void updateStatus(TransferStatus newStatus) {
        this.setStatus(newStatus);
        this.setStatusUpdatedAt(Instant.now());
    }

    public Duration processingDuration() {
        return Duration.between(this.getCreatedAt(), this.getStatusUpdatedAt());
    }
}
