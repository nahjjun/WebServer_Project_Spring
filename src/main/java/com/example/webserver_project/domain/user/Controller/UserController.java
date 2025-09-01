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


// UserController는 사용자 리소스 관련 작업만 수행한다
// 즉, 이미 인증된 사용자만 가능한 작업을 정의하는 것이다. => SecurityContextHolder에서 인증된 사용자 정보를 꺼내쓴다
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
