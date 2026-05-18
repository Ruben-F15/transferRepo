package com.microservice.transferservice.controller;

import com.microservice.transferservice.dto.CreateTransferRequestDTO;
import com.microservice.transferservice.dto.TransferAcceptedResponseDTO;
import com.microservice.transferservice.service.TransferService;
import jakarta.validation.Valid;
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
public class TransferController {

    private final TransferService transferService;

  /**
     * Endpoint público que recibe la solicitud de transferencia.
     * SOLO su trabajo es enviar el evento inicial al canal Kafka.
     *
     * @param request Contiene [sourceId, destinationId, amount].
     * @return 202 Accepted, indicando que la orden ya fue aceptada y se procesará.
     */
    @PostMapping("/transfer")
    public ResponseEntity<TransferAcceptedResponseDTO> iniciarTransferencia(@Valid @RequestBody CreateTransferRequestDTO request) {
        transferService.createTransfer(request);
        // Respuesta Inmediata (Asíncrono).
        // No esperamos el resultado. Simplemente, confirmamos que la orden fue recibida y está en cola de procesamiento.
        return ResponseEntity.accepted().body(new TransferAcceptedResponseDTO("Transferencia solicitada. Estaremos notificando el resultado en breve."));
    }
}
