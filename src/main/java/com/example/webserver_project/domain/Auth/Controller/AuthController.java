package com.example.webserver_project.domain.Auth.Controller;


import com.example.webserver_project.domain.Auth.Dto.request.LoginRequestDto;
import com.example.webserver_project.domain.Auth.Dto.response.LoginResponseDto;
import com.example.webserver_project.domain.Auth.Sevice.AuthService;
import com.example.webserver_project.global.response.GlobalWebResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")

// AuthController는 인증 관련 작업만 수행한다
// 토큰 발급/인증 흐름 관리에만 집중함 - 로그인, 회원가입, 토큰 재발급, 로그아웃
public class AuthController {
    private final AuthService authService;


    // 사용자 정보를 바탕으로 로그인 및 jwt 발급 수행
    @PostMapping("/login")
    public ResponseEntity<GlobalWebResponse<LoginResponseDto>> login(@RequestBody @Valid LoginRequestDto loginRequestDto, HttpServletResponse response) {
        LoginResponseDto dto = authService.login(loginRequestDto, response);
        return ResponseEntity.ok(GlobalWebResponse.success("로그인 성공", dto));
    }

    @PostMapping("/logout")
    public ResponseEntity<GlobalWebResponse<Void>> logout(HttpServletRequest request, HttpServletResponse response){
        authService.logout(request, response);
        return ResponseEntity.ok(GlobalWebResponse.success("로그아웃 성공"));
    }

    @PostMapping("/refresh")
    public ResponseEntity<GlobalWebResponse<Void>> reissueAccessToken(HttpServletRequest request, HttpServletResponse response) {
        authService.reissueAccessToken(request, response);
        return ResponseEntity.ok(GlobalWebResponse.success("Access Token 재발급 성공"));
    }



}
