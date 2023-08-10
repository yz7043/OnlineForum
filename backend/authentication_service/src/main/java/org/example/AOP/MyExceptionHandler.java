package org.example.AOP;

import org.example.dto.response.StatusResponse;
import org.example.exception.CantRegisterException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class MyExceptionHandler {
    @ExceptionHandler(CantRegisterException.class)
    public ResponseEntity<StatusResponse> handleCantRegisterException(CantRegisterException ex) {
        StatusResponse statusResponse = new StatusResponse();
        statusResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
        statusResponse.setSuccess(false);
        statusResponse.setMessage(ex.getMessage());

        return new ResponseEntity<>(statusResponse, HttpStatus.BAD_REQUEST);
    }
}
