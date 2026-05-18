package com.microservice.transferservice.kafka.producer;

import com.microservice.transferservice.kafka.event.TransferRequestedEvent;
import com.microservice.transferservice.kafka.topics.KafkaTopics;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.MDC;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransferEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendTransferRequestEvent(TransferRequestedEvent transferRequested) {

        String correlationId = MDC.get("correlationId");

        ProducerRecord<String, Object> record = new ProducerRecord<>(
                KafkaTopics.TRANSFER_REQUESTED.getTopic(),
                transferRequested.sourceUserId(),
                transferRequested
        );

        if (correlationId != null) { // enviamos en header el correlationalId para tracking.
            record.headers().add("correlationId", correlationId.getBytes());
        }

        kafkaTemplate.send(record);
    }
}
