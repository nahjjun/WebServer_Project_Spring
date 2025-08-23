package com.example.webserver_project.domain.user.Dto;


// Jwt 로직 내부에서 인증 유저 정보를 저장해 둘 dto


import com.example.webserver_project.global.jwt.RoleType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@NoArgsConstructor
public class JwtUserInfoDto {
    private Long userId;
    private String email;
    private String password;
    private String name;
    private RoleType role;
}
