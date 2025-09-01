package com.example.webserver_project.domain.user.Controller;


import com.example.webserver_project.domain.user.Dto.JoinRequestDto;
import com.example.webserver_project.domain.user.Dto.JwtUserInfoDto;
import com.example.webserver_project.domain.user.Dto.LoginRequestDto;
import com.example.webserver_project.domain.user.Dto.JoinResponseDto;
import com.example.webserver_project.domain.user.Service.AuthService;
import com.example.webserver_project.domain.user.Service.UserService;
import com.example.webserver_project.global.jwt.JwtUtil;
import com.example.webserver_project.global.response.GlobalWebResponse;
import com.example.webserver_project.global.status.SuccessStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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
    private final AuthenticationManager authenticationManager; // 인증 처리의 진입점이다. 여러 AuthenticationProvider에게 인증을 시도함
    private final JwtUtil jwtUtil;
    private final AuthService authService;


    // 사용자 정보를 바탕으로 로그인 및 jwt 발급 수행
    @PostMapping("/login")
    public ResponseEntity<GlobalWebResponse<String>> login(@RequestBody @Valid LoginRequestDto loginRequestDto) {
        // 인증 진입점인 AuthenticationManager를 사용하여
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDto.getEmail(), loginRequestDto.getPassword())
        );
            // ㄴ> 내부적으로 AuthenticationProvider(DaoAuthenticationProvider)
                // -> UserDetailsService.loadUserByUsername(email)로 DB에서 사용자 로드
                // -> PasswordEncoder.matches(raw, encoded)로 비번 검증
                // -> 성공 시 인증 완료 Authentication 반환
            // 위 과정을 통해 로그인 인증 과정을 내부적으로 수행한다. 따라서, UserDetailsService를 구현해놓고 PasswordEncoder 빈 등록을 해주어야 하는 것이다.


        JwtUserInfoDto principal = (JwtUserInfoDto) auth.getPrincipal();
            // auth.getPrincipal() : 현재 로그인한 사용자 정보를 가져오는 메서드

        // JwtUerInfoDto, jwtUtil를 이용하여 token 생성
        // token claim에 사용자 정보를 담기 때문에, 굳이 TokenResponseDto를 선언할 필요가 없다
        String token = jwtUtil.createAccessToken(principal);
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
