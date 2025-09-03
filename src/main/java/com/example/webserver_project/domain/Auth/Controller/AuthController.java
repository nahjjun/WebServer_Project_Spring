package com.example.webserver_project.domain.Auth.Controller;


import com.example.webserver_project.domain.Auth.Dto.request.JoinRequestDto;
import com.example.webserver_project.domain.Auth.Dto.request.LoginRequestDto;
import com.example.webserver_project.domain.Auth.Dto.response.JoinResponseDto;
import com.example.webserver_project.domain.Auth.Sevice.AuthService;
import com.example.webserver_project.global.response.GlobalWebResponse;
import com.example.webserver_project.global.status.SuccessStatus;
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
    public ResponseEntity<GlobalWebResponse<String>> login(@RequestBody @Valid LoginRequestDto loginRequestDto) {
        String token = authService.login(loginRequestDto);
        GlobalWebResponse<String> response = GlobalWebResponse.success(SuccessStatus.LoginOk.getCode(), SuccessStatus.LoginOk.getMessage(), token);
        return ResponseEntity
                .status(SuccessStatus.LoginOk.getStatus())
                .body(response);
    }

    // UserService의 join() 함수를 이용하여 회원가입 진행
    @PostMapping("/join")
    public ResponseEntity<GlobalWebResponse<JoinResponseDto>> join(@RequestBody @Valid JoinRequestDto joinRequestDto){
        JoinResponseDto joinResponseDto = authService.join(joinRequestDto);

        // 만약 RESTful API 규약을 엄격하게 지킨다면, Location 헤더를 사용해서 HTTP 표준인 "201 Created" 응답의 관례를 지키면 된다.
        GlobalWebResponse<JoinResponseDto> response = GlobalWebResponse.success(SuccessStatus.JoinOk.getCode(),  SuccessStatus.JoinOk.getMessage(), joinResponseDto);

        return ResponseEntity
                .status(SuccessStatus.JoinOk.getStatus())
                .body(response);
    }



    @PostMapping("/refresh")
    public ResponseEntity<GlobalWebResponse<String>> refresh(){

    }



}
