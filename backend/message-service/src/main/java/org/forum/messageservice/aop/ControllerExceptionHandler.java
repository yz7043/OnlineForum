package org.forum.messageservice.aop;

import org.forum.messageservice.dto.response.GeneralResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerExceptionHandler {
    @ExceptionHandler(value = {RuntimeException.class})
    public ResponseEntity handleRuntimeException(RuntimeException e) {
        return new ResponseEntity(GeneralResponse.builder()
                .message(e.getMessage())
                .build(), HttpStatus.OK);
    }
}
