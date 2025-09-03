package com.example.webserver_project.infra.redis;

import java.util.Optional;

// refresh 토큰을 저장해주는 인터페이스
public interface RefreshTokenStore {
    // username : 사실상 email
    // refreshToken : refreshToken
    // ttlMillis : Time To Live 밀리세컨드 값
    void save(String username, String refreshToken, long ttlMillis); // 로그인 (refreshToken 저장)
    Optional<String> find(String username); // refresh token 존재하는지 & 만료되지는 않았는지 확인하는 함수
    void delete(String username); // 로그아웃 함수 (refresh token 제거)
}
