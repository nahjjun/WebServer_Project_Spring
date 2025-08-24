package com.example.webserver_project.global.exception;

// JWT 인증 과정에서 Spring Security에서 권한(Role)이 부족하여 접근이 거부될 때 호출되는 핸들러


import com.example.webserver_project.global.response.GlobalWebResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j(topic = "FORBIDDEN_EXCEPTION_HANDLER")
@RequiredArgsConstructor
@Component
public class UserAccessDeniedHandler implements AccessDeniedHandler {
    private final ObjectMapper objectMapper; // Java<->Json 직렬화/역직렬화 기능을 제공하는 클래스

    // Filter에서 적용시킬 함수. 인증은 됐지만 권한이 부족할 때 실행시킬 함수
    // 매개변수로 클라이언트의 요청 및 응답, 접근 거부 예외 객체를 받음
        // 1. 접근 거부가 발생하면 로그를 기록함
        // 2. GlobalWebResponse 객체를 생성하여 HTTP 상태 코드, 예외 메세지, 현재 시간을 포함함
        // 3. 객체를 JSON 형식으로 직렬화하여 응답 본문에 작성하고 응답 상태 코드를 403 FORBIDDEN으로 설정
    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {
        log.error("권한 없음", accessDeniedException);

        GlobalWebResponse<?> errorResponse = GlobalWebResponse.error("403", "접근 권한이 없습니다.");
        response.setStatus(HttpStatus.FORBIDDEN.value()); // Http 상태 코드를 403(Forbidden)으로 설정함
        response.setContentType(MediaType.APPLICATION_JSON_VALUE); // 응답 본문 type을 "application/json"으로 지정
            // ㄴ> MediaType : HTTP 통신에서 전송되는 파일의 형식과 내용을 나타내는 식별자
            // ㄴ> 요청과 응답에서 클라이언트-서버가 주고받을 데이터의 형식을 명시하기 위해 사용됨
        response.setCharacterEncoding("UTF-8"); // 응답 문자 인코딩을 utf-8로 지정
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        // ㄴ> ObjectMapper로 자바 객체->JSON 문자열로 직렬화. 이후, 문자 스트림(Writer)으로 전송한다.
        // ObjectMapper를 사용하면 자동으로 JSON 직렬화가 가능하다. 따라서 ResponseEntity가 클라이언트-서버간에 이동할 때도 내부적으로 ObjectMapper가 사용된다.

    }



}
