package com.example.webserver_project.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // CSRF 비활성화 (POST 테스트 시 편의)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/users/join").permitAll() // 회원가입 허용
                        .anyRequest().authenticated() // 그 외 요청은 인증 필요
                );
        return http.build();
    }
}
