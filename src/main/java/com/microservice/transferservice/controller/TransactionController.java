package com.microservice.transferservice.controller;

import com.microservice.transferservice.dto.TransferRequestEventDTO;
import com.microservice.transferservice.kafka.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/transacciones")
@RequiredArgsConstructor
public class TransactionController {

    private final KafkaProducerService producerService;

  /**
     * Endpoint público que recibe la solicitud de transferencia.
     * SOLO su trabajo es enviar el evento inicial al canal Kafka.
     *
     * @param request Contiene [sourceId, destinationId, amount].
     * @return 202 Accepted, indicando que la orden ya fue aceptada y se procesará.
     */
    @PostMapping("/transferir")
    public ResponseEntity<String> iniciarTransferencia(@RequestBody TransferRequestEventDTO request) {
        // 1. Validación mínima de entrada.
        if (request == null || request.amount() == null || request.amount().compareTo(BigDecimal.ZERO) <= 0) {
            return ResponseEntity.badRequest().body("Monto inválido.");
        }

        // 2. **ACCIÓN CRÍTICA:** Publicar el evento inicial de forma asíncrona.
        producerService.publishTransferRequest(
                request.sourceAccountId(),
                request.destinationAccountId(),
                request.amount()
        );

        // 3. Respuesta Inmediata (Asíncrono).
        // No esperamos el resultado. Simplemente confirmamos que la orden fue recibida y está en cola de procesamiento.
        return ResponseEntity.accepted().body("Transferencia solicitada. Estaremos notificando el resultado en breve.");
    }


}
