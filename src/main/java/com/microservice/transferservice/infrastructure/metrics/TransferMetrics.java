package com.microservice.transferservice.infrastructure.metrics;

import com.microservice.transferservice.domain.enums.TransferStatus;
import com.microservice.transferservice.repository.TransferRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class TransferMetrics {

    private final TransferRepository transferRepository;
    private final Counter transferCreatedCounter;
    private final Counter transferCompletedCounter;
    private final Counter transferFailedCounter;
    private final Timer transferProcessingTimer;


    public TransferMetrics(TransferRepository transferRepository, MeterRegistry meterRegistry) {
        this.transferRepository = transferRepository;

        //metrica contador de transferencias creadas/solicitadas
        this.transferCreatedCounter =
                Counter.builder("business.transfer.created")
                        .description("Transferencias creadas")
                        .register(meterRegistry);

        //metrica contador de transferencias completadas
        this.transferCompletedCounter =
                Counter.builder("business.transfer.completed")
                        .description("Transferencias completadas")
                        .register(meterRegistry);

        //metrica contador de transferencias fallidas
        this.transferFailedCounter =
                Counter.builder("business.transfer.failed")
                        .description("Transferencias fallidas")
                        .register(meterRegistry);

        //metrica timer para controlar el tiempo que tarda en completarse una transferencia
        this.transferProcessingTimer = Timer.builder("business.transfer.processing.time")
                .description("Tiempo empleado en procesar una transferencia")
                .publishPercentileHistogram()
                .register(meterRegistry);

        //metrica de transferencias pendientes actuales
        Gauge.builder("business.transfer.pending",
                transferRepository,
                repo -> repo.countByStatus(TransferStatus.PENDING))
                .description("Transferencias pendientes")
                .register(meterRegistry);

        //metrica de transferencias en progreso actuales
        Gauge.builder("business.transfer.in_progress",
                        transferRepository,
                        repo -> repo.countByStatus(TransferStatus.DEBITED) + repo.countByStatus(TransferStatus.FUNDS_RESERVED))
                .description("Transferencias procesándose")
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

    public void recordTransferDuration(Duration duration) {
        transferProcessingTimer.record(duration);
    }
}
