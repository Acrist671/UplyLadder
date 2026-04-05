package com.otsos.userservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleException(ApiException e) {

        ErrorResponse body = new ErrorResponse(
                e.getStatusCode(),
                e.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(body, HttpStatus.valueOf(e.getStatusCode()));
    }
}
