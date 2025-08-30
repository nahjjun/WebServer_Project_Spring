package com.example.webserver_project.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class PasswordEncoderConfig {

    // BCryptPasswordEncoder : Spring Security 프레임워크에서 제공하는 비밀번호를 암호화하는 데 사용할 수 있는 메서드를 가진 클래스
    // BCrypt 해싱 함수를 사용해서 비밀번호를 인코딩할 수 있음
    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
