package com.alpha.theaterservice.exception;


public class InvalidScreenNumberException extends RuntimeException {

    public InvalidScreenNumberException(String message) {
        super(message);
    }
}