package com.microservice.transferservice.dto;


import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateTransferRequestDTO(
        @NotBlank(message = "Destination user cannot be blank")
        String destinationUserId,

        @NotNull(message = "Amount cannot be null")
        @DecimalMin(value = "0.01", message = "Amount must be greater than 0.00")
        BigDecimal amount
) {}
//        @NotBlank(message = "Destination account number cannot be blank")
//        String destinationAccountNumber,
//
//        @NotNull(message = "Amount cannot be null")
//        @DecimalMin(value = "0.01", message = "Amount must be greater than 0.00")
//        BigDecimal amount