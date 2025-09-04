package com.example.webserver_project.global.exception;

import com.example.webserver_project.global.exception.model.BaseErrorCode;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum GlobalErrorCode implements BaseErrorCode {
    INVALID_INPUT_VALUE("G001", "유효하지 않은 입력입니다.", HttpStatus.BAD_REQUEST), // 400 에러
    RESOURCE_NOT_FOUND("G002", "요청한 리소스를 찾을 수 없습니다.", HttpStatus.NOT_FOUND), // 404 에러
    INTERNAL_SERVER_ERROR("G003", "서버 내부 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR); // 500 에러

    private final String code;
    private final String message;
    private final HttpStatus status;

}
