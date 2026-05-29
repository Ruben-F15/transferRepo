package com.microservice.transferservice.domain.enums;

public enum TransferStatus {
    PENDING,

    FUNDS_RESERVED,
    RESERVE_FAILED,

    DEBITED,
    DEBIT_FAILED,

    FUNDS_CREDITED,
    CREDIT_FAILED,

    REFUND_COMPLETED,
    REFUND_FAILED,

    COMPLETED,
    FAILED
}
