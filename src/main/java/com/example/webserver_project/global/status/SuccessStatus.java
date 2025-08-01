package com.example.webserver_project.global.status;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SuccessStatus {
    // 회원가입 성공
    JoinOk(HttpStatus.OK, true, "JS000", "회원가입 성공"),
    // 로그인 성공
    LoginOk(HttpStatus.OK, true, "LS000", "로그인 성공"),
    // 탈퇴 성공
    DeleteOk(HttpStatus.OK, true, "DS000", "회원 탈퇴 성공");

    private final HttpStatus status;
    private final boolean success;
    private final String code;
    private final String message;
}
