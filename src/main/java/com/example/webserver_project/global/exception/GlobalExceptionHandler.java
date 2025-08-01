package com.example.webserver_project.global.exception;

import com.example.webserver_project.global.response.GlobalWebResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 비즈니스 로직 예외 처리
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<GlobalWebResponse<?>> handleIllegalArgumentException(IllegalArgumentException e){
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(GlobalWebResponse.error("400", e.getMessage()));
    }


    // Exception 최후의 보루 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<GlobalWebResponse<?>> handleException(Exception e){
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(GlobalWebResponse.error("500", e.getMessage()));
    }

}
