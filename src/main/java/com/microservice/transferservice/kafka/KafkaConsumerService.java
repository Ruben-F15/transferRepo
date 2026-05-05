package com.microservice.transferservice.kafka;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;


/**
 * Este servicio es el CORE del orquestador de la Saga. 
 * Escucha de Kafka y coordina la lógica de negocio.
 * Su lógica debe ser atómica dentro de una transacción.
 */
@AllArgsConstructor
@Service
public class KafkaConsumerService {

}
