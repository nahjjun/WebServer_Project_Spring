package com.example.webserver_project.domain.user.Controller;

import com.example.webserver_project.domain.user.Dto.DeleteRequestDto;
import com.example.webserver_project.domain.user.Dto.JoinRequestDto;
import com.example.webserver_project.domain.user.Dto.LoginRequestDto;
import com.example.webserver_project.domain.user.Dto.UserResponseDto;
import com.example.webserver_project.domain.user.Service.UserService;
import com.example.webserver_project.global.response.GlobalWebResponse;
import com.example.webserver_project.global.status.ErrorStatus;
import com.example.webserver_project.global.status.SuccessStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
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
        try{
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
        } catch (IllegalArgumentException e){
            // badRequest : 상태코드 400
            return ResponseEntity
                    .status(ErrorStatus.JoinFail.getStatus())
                    .body(GlobalWebResponse.error("400", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(ErrorStatus.JoinFail.getStatus())
                    .body(GlobalWebResponse.error(
                            ErrorStatus.JoinFail.getCode(),
                            e.getMessage()));
        }
    }

    // 로그인
    // 로그인 성공 시 로그인된 사용자 객체를 return해준다,
    @PostMapping("/login")
    public ResponseEntity<GlobalWebResponse<UserResponseDto>> login(@RequestBody @Valid LoginRequestDto loginRequest) {
        try{
            UserResponseDto user = userService.login(loginRequest);
            System.out.println("해당 계정 존재");
            GlobalWebResponse<UserResponseDto> response = GlobalWebResponse.success(SuccessStatus.LoginOk.getCode(), SuccessStatus.LoginOk.getMessage(), user);
            return ResponseEntity.status(SuccessStatus.LoginOk.getStatus()).body(response);
        } catch (IllegalArgumentException e){
//            e.printStackTrace();
//            Map<String, String> error = new HashMap<>();
//            error.put("error", "ILLEGAL ARGUMENT ERROR");
//            error.put("message", e.getMessage());
//            return ResponseEntity.badRequest().body(error);
            return ResponseEntity.status(ErrorStatus.LoginFail.getStatus()).body(GlobalWebResponse.error("400", e.getMessage()));
        }
        catch (Exception e) {
//            e.printStackTrace();
//            Map<String, String> error = new HashMap<>();
//            error.put("error", "INTERNAL SERVER ERROR");
//            error.put("message", e.getMessage());
//            return ResponseEntity.internalServerError().body(error);
            return ResponseEntity
                    .status(ErrorStatus.LoginFail.getStatus())
                    .body(GlobalWebResponse.error(
                            ErrorStatus.LoginFail.getCode(),
                            e.getMessage()
                    ));
        }
    }

    // 회원 탈퇴
    @PostMapping("/delete")
    public ResponseEntity<GlobalWebResponse<String>> delete(@RequestBody @Valid DeleteRequestDto deleteRequest) {
        try {
            userService.delete(deleteRequest);
            GlobalWebResponse<String> response = GlobalWebResponse.success(SuccessStatus.DeleteOk.getCode(), SuccessStatus.DeleteOk.getMessage(), null);
            return ResponseEntity
                    .status(SuccessStatus.DeleteOk.getStatus())
                    .body(response);
        } catch (IllegalArgumentException e) {
//            e.printStackTrace();
//            Map<String, String> error = new HashMap<>();
//            error.put("error", "ILLEGAL ARGUMENT ERROR");
//            error.put("message", e.getMessage());
//            return ResponseEntity.badRequest().body(error);
            return ResponseEntity
                    .status(ErrorStatus.DeleteFail.getStatus())
                    .body(GlobalWebResponse.error("400", e.getMessage()));
        } catch (Exception e) {
//            e.printStackTrace();
//            Map<String, String> error = new HashMap<>();
//            error.put("error", "INTERNAL SERVER ERROR");
//            error.put("message", e.getMessage());
//            return ResponseEntity.internalServerError().body(error);
            return ResponseEntity
                    .status(ErrorStatus.DeleteFail.getStatus())
                    .body(GlobalWebResponse.error(
                            ErrorStatus.DeleteFail.getCode(),
                            e.getMessage()));
        }

    }


}
