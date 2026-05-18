package com.microservice.transferservice.exception;

public class EqualSourceAndDestinationUserIdException extends RuntimeException {
    public EqualSourceAndDestinationUserIdException(String message) {
        super(message);
    }
}
