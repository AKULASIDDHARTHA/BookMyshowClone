package com.alpha.customerservice.exception;

public class CustomerAlreadyExistWithPhoneException extends RuntimeException {

    public CustomerAlreadyExistWithPhoneException() {
        super("Customer Already Exists With This Phone Number");
    }
}