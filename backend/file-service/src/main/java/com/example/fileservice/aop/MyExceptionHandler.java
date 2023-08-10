package com.example.fileservice.aop;

import com.example.fileservice.dto.response.common.GeneralResponse;
import com.example.fileservice.dto.response.common.StatusResponse;
import com.example.fileservice.exception.ObjectAlreadyExistsException;
import com.example.fileservice.exception.S3KeyNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class MyExceptionHandler {
    //sssssss
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<GeneralResponse<StatusResponse>> handleAllUncaughtException(RuntimeException ex, HttpServletRequest request) {
        StatusResponse statusResponse = new StatusResponse();
        statusResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        statusResponse.setSuccess(false);
        statusResponse.setMessage(ex.getMessage());

        GeneralResponse<Object> generalResponse = GeneralResponse.builder()
                .status(statusResponse)
                .data(null)
                .build();

        return new ResponseEntity<GeneralResponse<StatusResponse>>((MultiValueMap<String, String>) generalResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
