package com.example.webserver_project.global.status;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorStatus {
    JoinFail(HttpStatus.BAD_REQUEST, false, "JF000", "회원가입 실패"),
    LoginFail(HttpStatus.BAD_REQUEST, false, "LF000", "로그인 실패"),
    DeleteFail(HttpStatus.BAD_REQUEST, false, "DF000", "회원 탈퇴 실패");

    private final HttpStatus status;
    private final boolean success;
    private final String code;
    private final String message;
}
