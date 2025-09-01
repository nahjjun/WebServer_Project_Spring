package com.example.webserver_project.domain.user.Dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
// refresh 요청이 들어왔을 때 값을 받을 dto
public class RefreshRequestDto {
    private final String refreshToken;
}
