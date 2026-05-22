package com.microservice.transferservice.kafka.consumer;

import com.microservice.transferservice.kafka.event.FundsCreditedEvent;
import com.microservice.transferservice.kafka.event.FundsDebitedEvent;
import com.microservice.transferservice.kafka.event.FundsReservationFailedEvent;
import com.microservice.transferservice.kafka.event.FundsReservedEvent;
import com.microservice.transferservice.kafka.producer.TransferEventProducer;
import com.microservice.transferservice.service.TransferService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccountEventListener {

    private final TransferService transferService;
    private final TransferEventProducer transferEventProducer;

    @KafkaListener(topics = "transfer.funds.reserved")
    public void handleTransferFundsReserved(FundsReservedEvent fundsReservedEvent, @Header(value = "correlationId", required = false ) String correlationId) {
        try {
            if (correlationId != null) {
                MDC.put("correlationId", correlationId);
            }

            log.info("Received transfer funds reserved event for transactionId={}", fundsReservedEvent.transactionId());

            transferService.handleFundsReservedEvent(fundsReservedEvent);

        } finally {
            MDC.clear();
        }
    }

    @KafkaListener(topics = "transfer.funds.reservation.failed")
    public void handleTransferFundsReservationFailed(FundsReservationFailedEvent fundsReservationFailedEvent, @Header(value = "correlationId", required = false ) String correlationId) {
        try {
            if (correlationId != null) {
                MDC.put("correlationId", correlationId);
            }

            log.info("Received transfer funds reservation failed event for transactionId={}", fundsReservationFailedEvent.transactionId());

            transferService.handleFundsReservationFailedEvent(fundsReservationFailedEvent);

        } finally {
            MDC.clear();
        }
    }

    @KafkaListener(topics = "transfer.funds.debited")
    public void handleTransferFundsDebitedEvent(FundsDebitedEvent fundsDebitedEvent, @Header(value = "correlationId", required = false ) String correlationId) {
        try {
            if (correlationId != null) {
                MDC.put("correlationId", correlationId);
            }

            log.info("Received transfer funds debited event for transactionId={}", fundsDebitedEvent.transactionId());

            transferService.handleFundsDebitedEvent(fundsDebitedEvent);

        } finally {
            MDC.clear();
        }
    }

    @KafkaListener(topics = "transfer.funds.credited")
    public void handleTransferFundsCreditedEvent(FundsCreditedEvent fundsCreditedEvent, @Header(value = "correlationId", required = false ) String correlationId) {
        try {
            if (correlationId != null) {
                MDC.put("correlationId", correlationId);
            }

            log.info("Received transfer funds credited event for transactionId={}", fundsCreditedEvent.transactionId());

            transferService.handleFundsCreditedEvent(fundsCreditedEvent);

        } finally {
            MDC.clear();
        }
    }


}
