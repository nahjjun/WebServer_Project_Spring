package com.example.webserver_project.domain.user.Dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class TokenResponseDto {
    private final String tokenType; // 토큰 타입
    private final String accessToken; // 실제 엑세스 토큰
    private final long expiresIn; // 만료까지 남은 시간(초 단위)
    private final UserResponseDto user; // 사용자 정보
}
