package com.example.intermediate.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class CustomExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleApiRequestException(MethodArgumentNotValidException ex) {
        List<RestApiException> errors=new ArrayList<>();

        for( FieldError field : ex.getBindingResult().getFieldErrors()){
            errors.add(new RestApiException(field.getField(),field.getDefaultMessage()));
        }

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errors);
    }
}
