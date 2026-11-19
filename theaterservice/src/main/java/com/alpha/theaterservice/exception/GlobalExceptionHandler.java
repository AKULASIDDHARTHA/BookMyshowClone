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
    
    @ExceptionHandler(TheaterNotFoundException.class)
    public ResponseEntity<ResponseStructure<String>> handleTheaterNotFound(
            TheaterNotFoundException ex) {
        ResponseStructure<String> response = new ResponseStructure<>();
        response.setStatuscode(HttpStatus.NOT_FOUND.value());
        response.setMessage(ex.getMessage());
        response.setData(null);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(MovieNotFoundException.class)
    public ResponseEntity<ResponseStructure<String>> handleMovieNotFound(
            MovieNotFoundException ex) {

        ResponseStructure<String> response = new ResponseStructure<>();

        response.setStatuscode(HttpStatus.NOT_FOUND.value());
        response.setMessage(ex.getMessage());
        response.setData(null);

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(InvalidScreenNumberException.class)
    public ResponseEntity<ResponseStructure<String>> handleInvalidScreen(
            InvalidScreenNumberException ex) {

        ResponseStructure<String> response = new ResponseStructure<>();

        response.setStatuscode(HttpStatus.BAD_REQUEST.value());
        response.setMessage(ex.getMessage());
        response.setData(null);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ShowAlreadyExistsException.class)
    public ResponseEntity<ResponseStructure<String>> handleShowExists(
            ShowAlreadyExistsException ex) {

        ResponseStructure<String> response = new ResponseStructure<>();

        response.setStatuscode(HttpStatus.CONFLICT.value());
        response.setMessage(ex.getMessage());
        response.setData(null);

        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }
    
    @ExceptionHandler(ShowNotFoundException.class)
    public ResponseEntity<ResponseStructure<String>> handleShowNotFound(
            ShowNotFoundException ex) {

        ResponseStructure<String> response = new ResponseStructure<>();

        response.setStatuscode(HttpStatus.NOT_FOUND.value());
        response.setMessage(ex.getMessage());
        response.setData(null);

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
}
