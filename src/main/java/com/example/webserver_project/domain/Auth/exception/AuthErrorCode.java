package com.example.webserver_project.domain.Auth.exception;

import com.example.webserver_project.global.exception.model.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements BaseErrorCode {
    LOGIN_FAIL("AUTH_401_001", "로그인 처리 중 오류가 발생했습니다.", HttpStatus.UNAUTHORIZED),
    USER_INFO_FAIL("AUTH_401_002", "사용자 정보 요청을 실패했습니다.", HttpStatus.UNAUTHORIZED),
    INVALID_AUTH_CONTEXT("AUTH_401_003", "SecurityContext에 인증 정보가 없습니다.", HttpStatus.UNAUTHORIZED),
    AUTHENTICATION_NOT_FOUND("AUTH_401_004", "로그인이 필요합니다.", HttpStatus.UNAUTHORIZED),
    INVALID_PASSWORD("AUTH_401_005", "비밀번호가 일치하지 않습니다.", HttpStatus.UNAUTHORIZED),

    TOKEN_FAIL("JWT_401_001", "액세스 토큰 요청을 실패했습니다.", HttpStatus.UNAUTHORIZED),
    INVALID_ACCESS_TOKEN("JWT_401_002", "유효하지 않은 액세스 토큰입니다.", HttpStatus.UNAUTHORIZED),
    INVALID_REFRESH_TOKEN("JWT_401_003", "유효하지 않은 리프레시 토큰입니다.", HttpStatus.UNAUTHORIZED),
    ACCESS_TOKEN_EXPIRED("JWT_401_004", "액세스 토큰이 만료되었습니다.", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_EXPIRED("JWT_401_005", "리프레시 토큰이 만료되었습니다.", HttpStatus.UNAUTHORIZED),
    UNSUPPORTED_TOKEN("JWT_401_006", "지원되지 않는 JWT 형식입니다.",  HttpStatus.UNAUTHORIZED),
    INVALID_SIGNATURE("JWT_401_007", "JWT 서명이 유효하지 않습니다.", HttpStatus.UNAUTHORIZED)
    ;

    private final String code;
    private final String message;
    private final HttpStatus status;
}
