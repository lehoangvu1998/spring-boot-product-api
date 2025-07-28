package com.eureka.store.exception;

import com.eureka.store.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalHandleExceptions {

    /*
     * Xử lý lỗi 404 khi không tìm thấy endpoint (No Handler Found Exception).
     * Yêu cầu cấu hình trong application.properties.
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoHandlerFoundException(NoHandlerFoundException ex , WebRequest webRequest){
        ErrorResponse response = new ErrorResponse(HttpStatus.NOT_FOUND.value(),
                "Endpoint not found. The requested URL does not exist on this server.",
                webRequest.getDescription(false));
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundData(NoSuchElementException ex , WebRequest webRequest){
        ErrorResponse response = new ErrorResponse(HttpStatus.NOT_FOUND.value(),
                "Data is not found in database.",
                webRequest.getDescription(false));
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundDataTwo(NullPointerException ex , WebRequest webRequest){
        ErrorResponse response = new ErrorResponse(HttpStatus.NOT_FOUND.value(),
                "Null Pointer. Please check logic and add check null",
                webRequest.getDescription(false));
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

}
