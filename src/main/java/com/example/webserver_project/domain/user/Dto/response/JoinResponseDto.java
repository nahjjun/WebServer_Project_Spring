package com.example.webserver_project.domain.user.Dto.response;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@Builder
public class JoinResponseDto {
    @NotBlank
    private final Long id;

    @NotBlank
    private final String name;

    @Email
    @NotBlank
    private final String email;

}
