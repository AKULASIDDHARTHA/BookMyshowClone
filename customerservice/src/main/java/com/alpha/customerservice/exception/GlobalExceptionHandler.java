package com.alpha.customerservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.alpha.customerservice.dto.ResponseStructure;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomerAlreadyExistWithPhoneException.class)
    public ResponseEntity<ResponseStructure<String>> handleCustomerAlreadyExist(
            CustomerAlreadyExistWithPhoneException ex) {

        ResponseStructure<String> responseStructure = new ResponseStructure<>();

        responseStructure.setStatuscode(HttpStatus.CONFLICT.value());

        responseStructure.setMessage(ex.getMessage());

        responseStructure.setData(null);

        return new ResponseEntity<ResponseStructure<String>>(
                responseStructure, HttpStatus.CONFLICT);
    }
    
    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<ResponseStructure<String>> handleCustomerNotFound(
            CustomerNotFoundException ex) {

        ResponseStructure<String> response = new ResponseStructure<>();

        response.setStatuscode(HttpStatus.NOT_FOUND.value());
        response.setMessage(ex.getMessage());
        response.setData(null);

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

}