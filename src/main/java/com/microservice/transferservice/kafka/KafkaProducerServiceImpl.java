package com.microservice.transferservice.kafka;

import com.microservice.transferservice.dto.TransferRequestEventDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Servicio encargado de la comunicación con Kafka, abstractando el template.
 */
@Service
@RequiredArgsConstructor
public class KafkaProducerServiceImpl implements KafkaProducerService {

    private final KafkaTemplate<String, TransferRequestEventDTO> kafkaTemplate;
    private static final String TOPIC_NAME = "transfer_requests";

    @Override
    public void publishTransferRequest(String sourceId, String destinationId, BigDecimal amount) {
        // Generamos un ID único para rastrear esta saga específica.
        String correlationId = java.util.UUID.randomUUID().toString();

        TransferRequestEventDTO event = new TransferRequestEventDTO(
            sourceId, 
            destinationId, 
            amount, 
            Long.parseLong(correlationId) // Usamos Long para el tipo de dato de evento si prefiere el cliente.
        );

        System.out.println(">>> [KAFKA] Publicando evento de solicitud de transferencia en el tópico: " + TOPIC_NAME);
        
        // Enviamos el evento al tópico de Kafka, usando el ID de la fuente como clave.
        kafkaTemplate.send(TOPIC_NAME, sourceId, event);
    }
}
