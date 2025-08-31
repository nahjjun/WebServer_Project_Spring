package com.example.webserver_project.domain.user.Controller;

import com.example.webserver_project.domain.user.Dto.*;
import com.example.webserver_project.domain.user.Service.UserService;
import com.example.webserver_project.global.response.GlobalWebResponse;
import com.example.webserver_project.global.status.SuccessStatus;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
//    // JWT 정상적으로 .env 설정 되었는지 디버깅용 코드
//    @Value("${jwt.secret:__missing__}")
//    private String secret;
//
//    @PostConstruct
//    void check() {
//        System.out.println("JWT secret = " + secret);
//    }

    private final UserService userService;

//    @Autowired
//    public UserController(UserService userService){
//        this.userService = userService;
//    }
// ㄴ> @RequiredArgsConstructor가 final 멤버 변수를 전부 생성해주므로, Autowired를 사용할
    // 필요가 없음!


    // 회원가입
    // 회원가입 시, 회원가입된 사용자 객체를 return해줌
    @PostMapping("/join")
    // @Validated로 그룹 유효성 검사 진행
    public ResponseEntity<GlobalWebResponse<UserResponseDto>> join(@RequestBody @Valid JoinRequestDto joinRequest) {
        UserResponseDto user = userService.register(joinRequest);

        // userService로부터 받은 user 객체를 GlobalWebResponse 객체의
        // 내부 데이터로 설정한다. 이 작업은 Cotroller에서 진행되는 것이 바람직한다.
        // Service는 비즈니스 로직에 집중하고, Controller가 응답 포맷을 결정
        // 하는 것이 가장 깔끔한 책임 분리이기 때문이다.
        // <GlobalWebResponse 객체 생성>
        GlobalWebResponse<UserResponseDto> response = GlobalWebResponse.success(SuccessStatus.JoinOk.getCode(), SuccessStatus.JoinOk.getMessage(), user);

        // ResponseEntity를 동적으로 설정
        // 내가 설정한 HttpStatus 상태로 설정
        return ResponseEntity.status(SuccessStatus.JoinOk.getStatus()).body(response);
    }

    // 로그인
    // 로그인 성공 시 로그인된 사용자 객체를 return해준다,
    @PostMapping("/login")
    public ResponseEntity<GlobalWebResponse<TokenResponseDto>> login(@RequestBody @Valid LoginRequestDto loginRequest) {
        TokenResponseDto user = userService.login(loginRequest);
        System.out.println("해당 계정 존재");
        GlobalWebResponse<TokenResponseDto> response = GlobalWebResponse.success(SuccessStatus.LoginOk.getCode(), SuccessStatus.LoginOk.getMessage(), user);
        return ResponseEntity.status(SuccessStatus.LoginOk.getStatus()).body(response);
    }

    // 회원 탈퇴
    @PostMapping("/delete")
    public ResponseEntity<GlobalWebResponse<String>> delete(@RequestBody @Valid DeleteRequestDto deleteRequest) {
        userService.delete(deleteRequest);
        GlobalWebResponse<String> response = GlobalWebResponse.success(SuccessStatus.DeleteOk.getCode(), SuccessStatus.DeleteOk.getMessage(), null);
        return ResponseEntity
                .status(SuccessStatus.DeleteOk.getStatus())
                .body(response);
    }
}
