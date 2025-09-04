package com.example.webserver_project.domain.user.Controller;

import com.example.webserver_project.domain.user.Dto.request.DeleteRequestDto;
import com.example.webserver_project.domain.user.Dto.request.JoinRequestDto;
import com.example.webserver_project.domain.user.Dto.response.JoinResponseDto;
import com.example.webserver_project.domain.user.Service.UserService;
import com.example.webserver_project.global.response.GlobalWebResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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



// UserService의 join() 함수를 이용하여 회원가입 진행
    @PostMapping("/join")
    public ResponseEntity<GlobalWebResponse<JoinResponseDto>> join(@RequestBody @Valid JoinRequestDto joinRequestDto){
        JoinResponseDto joinResponseDto = userService.join(joinRequestDto);

        // 만약 RESTful API 규약을 엄격하게 지킨다면, Location 헤더를 사용해서 HTTP 표준인 "201 Created" 응답의 관례를 지키면 된다.
        GlobalWebResponse<JoinResponseDto> response = GlobalWebResponse.success(SuccessStatus.JoinOk.getCode(),  SuccessStatus.JoinOk.getMessage(), joinResponseDto);

        return ResponseEntity
                .status(SuccessStatus.JoinOk.getStatus())
                .body(response);
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
