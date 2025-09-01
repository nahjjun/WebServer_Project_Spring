package com.example.webserver_project.global.config;

import org.springframework.context.annotation.Configuration;

@Configuration


// CorsConfigurationSource 빈을 따로 등록해두면, SecurityConfig에서
// http.cors()를 켰을 때 Spring Security의 CorsFilter가 해당 빈을 자동으로 사용한다.
    // ㄴ> 즉, 브라우저의 CORS 요청을 Security 필터 단계에서 올바르게 처리하도록 하는 설정

public class CorsConfig {
    


}
