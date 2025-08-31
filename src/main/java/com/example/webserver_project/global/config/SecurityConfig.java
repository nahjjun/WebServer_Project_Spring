package com.example.webserver_project.global.config;

import com.example.webserver_project.domain.user.Service.CustomUserDetailsService;
import com.example.webserver_project.global.exception.UserAccessDeniedHandler;
import com.example.webserver_project.global.exception.UserAuthenticationEntryPoint;
import com.example.webserver_project.global.jwt.JwtAuthFilter;
import com.example.webserver_project.global.jwt.JwtUtil;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity // Spring Security의 웹 보안 기능을 활성화하는 어노테이션

@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true)
    // ㄴ> 메서드 수준에서의 보안 처리를 활성화 해주는 어노테이션
    // ㄴ> @Secure, @PreAuthorize 어노테이션 사용 가능함
@AllArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService; // username으로 DB에서 사용자를 조회하여 UserDetails로 반환해주는 서비스 클래스
    private final JwtUtil jwtUtil;
    private final UserAccessDeniedHandler accessDeniedHandler; // 인증은 됐지만 사용자의 권한(role)이 부족한 경우 사용할 핸들러
    private final UserAuthenticationEntryPoint authenticationEntryPoint; // 미인증된 사용자가 보호된 리소스에 접근 시 사용할 핸들러

    // 인증 없이 접근 가능한 화이트 리스트 URL 모음 String 배열 (로그인, 회원가입, 스웨거 등)
    private static final String[] AUTH_WHITELIST = {
        "/user/login", "/user/join", "/swagger-ui/**", "/api-docs", "swagger-ui-custom.html"
    };

    // 각 Request마다 해당 filterChain에 등록된 필터들이 순서대로 실행된다.
    // HttpSecurity는 SecurityFilterChain을 조립하는 Builder(DSL)이다.
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // 1. CSRF 비활성화, CORS 기본값 적용
        http.csrf(AbstractHttpConfigurer::disable); // CSRF 방어 기능 비활성화
            // ㄴ> JWT 기반 Stateless API에서는 "폼 로그인-세션 기반" 인증이 아니므로 CSRF 방어 기능이 필요없다
        http.cors(Customizer.withDefaults()); // Spring MVC에서 등록한 CORS 기본 설정값을 적용
            // ㄴ> Spring Security의 CORS 설정을 활성화하되, 등록된 기본 CORS 설정(CorsConfigurationSource 빈)을 그대로 쓰겠다는 뜻
            // ㄴ> 교차 출처 리소스 공유(Cross-Origin Resource Sharing, CORS)는 브라우저가 자신의 출처가 아닌 다른 어떤
            // 출처(도메인, 스킴 혹은 포트)로부터 자원을 로딩하는 것을 허용하도록 서버가 허가해주는 HTTP 헤더 기반 메커니즘
                    // ex) https://domain-a.com에서 제공되는 프론트엔트 JS 코드가 fetch()를 사용하여 https://domain-b.com/data.json에 요청하는 경우
            // 기존 웹 브라우저는 다른 도메인(origin)에 AJAX 요청을 보낼 때 보안 정책(Same-Origin Policy)때문에 차단한다
            // 이때, 서버가 "Access-Control-Allow-Origin", "Access-Control-Allow-Methods"같은 헤더를 내려주면, 브라우저가 요청을 허용한다.
                    // ex) 프론트 : http://localhost:3000 & 백엔드 : http://localhost:8080
                    // ㄴ> 서로 다른 origin이므로, CORS가 필요한 것이다.
            // 세밀한 설정을 위해서는 "CorsConfigurationSource" bean을 따로 등록해야한다


        // 2. 세션 관리 - 세션을 사용하지 않도록 설정한다. (JWT를 사용하기 위함임)
        http.sessionManagement(sessionManageMent -> sessionManageMent.sessionCreationPolicy(
                SessionCreationPolicy.STATELESS
        ));

        // 3. 폼 로그인 & HTTP Basic 끄기
        // 폼 로그인(세션)과 Basic 인증을 꺼서 JWT만 사용하도록 만든다.
        http.formLogin(AbstractHttpConfigurer::disable);
        http.httpBasic(AbstractHttpConfigurer::disable);

        // 4. JWT 필터 등록
        // UsernamePasswordAuthenticationFilter 앞에 직접 만든 JwtAuthFilter를 넣는다
        // 해당 필터로 JWT를 통한 인증 처리를 수행하게 한다.
        http.addFilterBefore(new JwtAuthFilter(customUserDetailsService, jwtUtil), UsernamePasswordAuthenticationFilter.class);
            // ㄴ> http.addFilterBefore("추가할 필터", 기존 필터)

        // 5. 예외 처리 핸들러 설정 - 인증 실패 및 접근 거부 예외를 처리하는 핸들러를 설정함
        http.exceptionHandling(e -> e
                .authenticationEntryPoint(authenticationEntryPoint) // authenticationEntryPoint(401) : 인증 자체가 없는 상태에서 보호된 자원에 접근할 때 사용하는 핸들러
                                            // ㄴ> 내부적으로 commence 함수를 오버라이드 해놓았다.
                .accessDeniedHandler(accessDeniedHandler)); // accessDeniedHandler(403) : 인중은 됐지만 권한이 부족한 경우 사용되는 핸들러
                                            // ㄴ> 내부적으로 handler 함수 정의해놓음

        // 6. 권한 규칙 작성
            // 1) 화이트 리스트에 있는 경로는 누구나 접근할 수 있도록 허용함
            // 2) 나머지 모든 경로는 @PreAuthorize 등의 메서드 수준 보안을 사용하여 접근을 제어함
        http.authorizeHttpRequests(authorize -> {
            authorize
                    .requestMatchers(AUTH_WHITELIST).permitAll()
                    .anyRequest().permitAll();
        });
        // authorizeHttpRequests() : Spring Security에서 URL 요청 별 인가(Authorization, 권한 부여)규칙을 설정하는 DSL 함수
        // 파라미터 : Customizer<AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry>
            // ㄴ> 의미 : 인가 규칙 빌터(AuthorizationManagerRequestMatcherRegistry)를 커스터마이징하라
        // .requestMatchers(...) : URL 패턴, HttpMethod, RequestMatcher 등을 지정할 수 있음

        return http.build(); // build() 함수로 HttpSecurity 객체를 빌드하여 SecurityFilterChain 객체를 생성함
    }

}
