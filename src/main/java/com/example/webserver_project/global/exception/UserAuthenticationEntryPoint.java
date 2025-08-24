package com.example.webserver_project.global.exception;

// 미인증 사용자가 보호된 리소스에 접근했을 때 호출되는 핸들러

import com.example.webserver_project.global.response.GlobalWebResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class UserAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper;

    // Filter에서 적용시킬 함수. 미인증 사용자가 보호 리소스에 접근했을 때 실행시킬 함수
    // 매개변수로 클라이언트의 요청 및 응답, 접근 거부 예외 객체를 받음
        // 1. 접근 거부가 발생하면 로그를 기록함
        // 2. GlobalWebResponse 객체를 생성하여 HTTP 상태 코드, 예외 메세지, 현재 시간을 포함함
        // 3. 객체를 JSON 형식으로 직렬화하여 응답 본문에 작성하고 응답 상태 코드를 401 UNAUTHORIZED으로 설정
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        log.error("인증되지 않은 사용자의 접근 시도: {}", request.getRequestURI(), authException);
        // ㄴ> "+"를 쓰지 않고, {}로 값을 바인딩 한다.

        GlobalWebResponse<?> errorResponse = GlobalWebResponse.error("401", "인증이 필요합니다.");

        response.setStatus(HttpStatus.UNAUTHORIZED.value()); // 미인증 상태 401(UNAUTHORIZED)로 설정해줌
                                                // ㄴ> Enum의 value() 함수는 열거 상수(401)을 int형으로 바꿔준다.
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        // ㄴ> ObjectMapper로 자바 객체->JSON 문자열로 직렬화. 이후, 문자 스트림(Writer)으로 전송한다.
        // ObjectMapper를 사용하면 자동으로 JSON 직렬화가 가능하다. 따라서 ResponseEntity가 클라이언트-서버간에 이동할 때도 내부적으로 ObjectMapper가 사용된다.
    }

}
