package com.microservice.transferservice.domain.enums;

public enum TransferStatus {
    PENDING,
    FUNDS_RESERVED,
    DEBITED,
    DEBIT_FAILED,
    CREDIT_FAILED,
    COMPLETED,
    FAILED
}
