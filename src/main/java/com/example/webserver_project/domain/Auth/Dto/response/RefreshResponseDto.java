package com.example.webserver_project.domain.Auth.Dto.response;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RefreshResponseDto {
    private final String accessToken;
    private final String refreshToken;
}
