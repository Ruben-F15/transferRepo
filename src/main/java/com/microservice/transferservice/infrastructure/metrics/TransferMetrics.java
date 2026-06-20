package com.microservice.transferservice.infrastructure.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class TransferMetrics {

    private final Counter transferCreatedCounter;
    private final Counter transferCompletedCounter;
    private final Counter transferFailedCounter;


    public TransferMetrics(MeterRegistry meterRegistry) {
        this.transferCreatedCounter =
                Counter.builder("business.transfer.created")
                        .description("Transferencias creadas")
                        .register(meterRegistry);
        this.transferCompletedCounter =
                Counter.builder("business.transfer.completed")
                        .description("Transferencias completadas")
                        .register(meterRegistry);
        this.transferFailedCounter =
                Counter.builder("business.transfer.failed")
                        .description("Transferencias fallidas")
                        .register(meterRegistry);
    }

    public void  incrementTransferCreatedCounter() {
        transferCreatedCounter.increment();
    }

    public void  incrementTransferCompletedCounter() {
        transferCompletedCounter.increment();
    }

    public void  incrementTransferFailedCounter() {
        transferFailedCounter.increment();
    }
}
