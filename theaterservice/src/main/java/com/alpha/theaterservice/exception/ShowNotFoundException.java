package com.alpha.theaterservice.exception;

public class ShowNotFoundException extends RuntimeException {

    public ShowNotFoundException(String message) {
        super(message);
    }
}