package com.example.webserver_project.global.exception;

import com.example.webserver_project.global.exception.model.BaseErrorCode;
import com.example.webserver_project.global.response.GlobalWebResponse;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {


    // CustomException 예외 처리
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<GlobalWebResponse<?>> handleCustomException(CustomException e) {
        BaseErrorCode errorCode = e.getErrorCode();
        log.error("Custom 오류 발생 : {}", e.getErrorCode());
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(GlobalWebResponse.error(errorCode.getCode(), errorCode.getMessage()));
    }


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
                .body(GlobalWebResponse.error("500", "예상치 못한 서버 오류가 발생했습니다."));
    }

}
