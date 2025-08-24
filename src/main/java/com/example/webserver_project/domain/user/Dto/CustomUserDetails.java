package com.example.webserver_project.domain.user.Dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

// UserDetails : 인증된 사용자의 아이디, 비밀번호, 권한 등을 포함하는 인터페이스
// 인증된 사용자의 정보를 SecurityContext에 보관할 때 사용된다.
    // ㄴ> SpringContext : 로그인 후 인증된 사용자의 정보들을 저장하는 인터페이스

@Getter
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {
    private final JwtUserInfoDto user; // JWT 인증 유저 정보를 담고 있는 DTO


    // getAuthorities() : DB의
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities(){

    }


}
