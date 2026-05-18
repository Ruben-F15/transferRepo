package com.microservice.transferservice.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EqualSourceAndDestinationUserIdException.class)
    public ResponseEntity<ApiError> handleTransferServiceException(EqualSourceAndDestinationUserIdException e, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                new ApiError(
                        Instant.now(),
                        HttpStatus.CONFLICT.value(),
                        "Same userId transfer conflict",
                        e.getMessage(),
                        request.getRequestURI(),
                        ErrorCode.SAME_USER_ID_TRANSFER_CONFLICT.name()
                )
        );
    }
}
