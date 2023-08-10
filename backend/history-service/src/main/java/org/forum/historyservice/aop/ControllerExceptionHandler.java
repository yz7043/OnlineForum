package org.forum.historyservice.aop;

import org.forum.historyservice.dto.response.GeneralResponse;
import org.forum.historyservice.dto.response.StatusResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerExceptionHandler {
    @ExceptionHandler(value = {RuntimeException.class})
    public ResponseEntity handleRuntimeException(RuntimeException e) {
        return new ResponseEntity(GeneralResponse.builder()
                .status(StatusResponse.builder()
                        .statusCode(400)
                        .message(e.getMessage())
                        .success(false)
                        .build())
                .build(), HttpStatus.OK);
    }
}
