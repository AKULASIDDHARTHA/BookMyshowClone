package com.alpha.theaterservice.exception;

public class ShowAlreadyExistsException extends RuntimeException {

    public ShowAlreadyExistsException(String message) {
        super(message);
    }
}
