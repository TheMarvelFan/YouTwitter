package com.mthree.backend.exceptionhandler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.mthree.backend.utils.ErrorType;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ErrorType.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorType> handleErrorType(ErrorType error) {
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

}
