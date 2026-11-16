package com.alpha.theaterservice.exception;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.alpha.theaterservice.dto.ResponseStructure;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TheaterAlreadyExistWithContactException.class)
    public ResponseEntity<ResponseStructure<String>> handleTheaterAlreadyExist(
            TheaterAlreadyExistWithContactException ex) {

        ResponseStructure<String> response = new ResponseStructure<>();

        response.setStatuscode(HttpStatus.CONFLICT.value());
        response.setMessage(ex.getMessage());
        response.setData(null);

        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }
}
