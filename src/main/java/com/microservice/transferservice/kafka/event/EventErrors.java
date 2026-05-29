package com.microservice.transferservice.kafka.event;

import lombok.Getter;

@Getter
public enum EventErrors {
    INSUFFICENT_FUNDS("insufficent.funds"),
    ACCOUNT_NOT_FOUND("account.not.found"),
    UNDEFINED_ERROR("undefined.error.handle.transfer.request"),
    EVENT_PUBLISH_FAILED("event.publish.failed");

    private final String error;
    EventErrors(String error) {
        this.error = error;
    }
}
