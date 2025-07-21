package com.example.webserver_project.domain.user.Dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class JoinRequestDto {
    @NotNull
    private String name;

    @NotNull
    @Email
    private String email;

    @NotNull
    private String password;
}
