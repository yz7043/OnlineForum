package org.forum.post_reply_service.aop;

import com.mongodb.MongoException;
import org.forum.post_reply_service.common.CommonResponse;
import org.forum.post_reply_service.common.StatusResponse;
import org.forum.post_reply_service.exception.ModifyImmutableResourceException;
import org.forum.post_reply_service.exception.PostStateTransferException;
import org.forum.post_reply_service.exception.ResourceNotFoundException;
import org.forum.post_reply_service.exception.UnauthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ExceptionAOP {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                       WebRequest request) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        CommonResponse<Object> response = CommonResponse
                .builder()
                .status(StatusResponse.builder().statusCode(HttpStatus.BAD_REQUEST.value()).success(false).build())
                .data(errors)
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<CommonResponse> handleResourceNotFound(ResourceNotFoundException ex){
        CommonResponse<Object> response = CommonResponse
                .builder()
                .status(StatusResponse.builder().statusCode(HttpStatus.NOT_FOUND.value()).success(false).build())
                .data(ex.getMessage())
                .build();
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({ModifyImmutableResourceException.class, PostStateTransferException.class})
    public ResponseEntity<CommonResponse> handleModifyImmutableResource(RuntimeException ex){
        CommonResponse<Object> response = CommonResponse
                .builder()
                .status(StatusResponse.builder().statusCode(HttpStatus.BAD_REQUEST.value()).success(false).build())
                .data(ex.getMessage())
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MongoException.class)
    public ResponseEntity<CommonResponse> handleGeneralMongoException(MongoException ex){
        CommonResponse<Object> response = CommonResponse
                .builder()
                .status(StatusResponse.builder().statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value()).success(false).build())
                .data(ex.getMessage())
                .build();
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<CommonResponse> handleUnauthorizedException(UnauthorizedException ex){
        CommonResponse<Object> response = CommonResponse
                .builder()
                .status(StatusResponse.builder().statusCode(HttpStatus.UNAUTHORIZED.value()).success(false).build())
                .data(ex.getMessage())
                .build();
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }
}
