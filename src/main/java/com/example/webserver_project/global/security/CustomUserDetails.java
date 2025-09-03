package com.example.webserver_project.global.security;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

// UserDetails : 인증된 사용자의 아이디, 비밀번호, 권한 등을 포함하는 인터페이스
// 인증된 사용자의 정보를 SecurityContext에 보관할 때 사용된다.
    // ㄴ> SpringContext : 로그인 후 인증된 사용자의 정보들을 저장하는 인터페이스

@Getter
@Builder
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {
    private final JwtUserInfoDto user; // JWT 인증 유저 정보를 담고 있는 DTO

    // getAuthorities() : DB의 Role(USER, ADMIN)을 꺼내서 "ROLE_" 접두사를 붙이고 Spring Security 전용 권한 객체(SimpleGrantedAuthority)로 변환하는 함수
    // 최종적으로 권한 목록을 반환한다.
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities(){
        List<String> roles = new ArrayList<>(); // 권한 목록을 담을 배열
        roles.add("ROLE_" + user.getRole().toString()); // 해당 사용자가 갖고 있는 권한에 "ROLE_" 접두사 붙여서 넣기

        return roles.stream()
                .map(SimpleGrantedAuthority::new) // String을 SimpleGrantedAuthority 객체(Spring Security 전용 권한 객체)로 매핑한다.
                .toList();
    }


    // 인증 시 비교할 비밀번호 반환
    @Override
    public String getPassword(){
        return user.getPassword();
    }

    // 인증 시 로그인 식별자 반환
    // 사용자 이메일 반환 (로그인은 이메일로 진행함)
    @Override
    public String getUsername(){
        return user.getEmail();
    }

    // 계정 만료 여부 반환 함수 (true면 만료되지 않는다)
    // 유효기간이 지난 계정을 막고 싶다면, DB 필드를 연결하여 false를 반환하도록 커스터마이징 해야함
    @Override
    public boolean isAccountNonExpired(){
        return true;
    }

    // 계정 잠김 여부 반환 함수
    // 로그인 실패 누적/관리자 제재 등으로 잠금 처리할 때 false로 설정
    @Override
    public boolean isAccountNonLocked(){
        return true;
    }

    // 자격 증명(비밀번호) 만료 여부 반환 함수
    // 주기적 비밀번호 변경 정책이 있다면 만료 로직에 맞춰 false 반환
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // 활성화 여부
    @Override
    public boolean isEnabled() {
        return true;
    }

    // 위 4가지 함수는 인증 성공 이후에도 접근을 차당할 수 있는 "상태 기반" 스위치 역할을 하는 함수



}
