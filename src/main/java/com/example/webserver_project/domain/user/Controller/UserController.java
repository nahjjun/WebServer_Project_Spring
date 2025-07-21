package com.example.webserver_project.domain.user.Controller;

import com.example.webserver_project.domain.user.Dto.DeleteRequestDto;
import com.example.webserver_project.domain.user.Dto.JoinRequestDto;
import com.example.webserver_project.domain.user.Dto.LoginRequestDto;
import com.example.webserver_project.domain.user.Dto.UserResponseDto;
import com.example.webserver_project.domain.user.Service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<?> join(@RequestBody @Valid JoinRequestDto joinRequest) {
        try{
            UserResponseDto user = userService.register(joinRequest);
            return ResponseEntity.ok(user);
        } catch (IllegalArgumentException e){
            e.printStackTrace();
            // badRequest : 상태코드 400
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "INTERNAL SERVER ERROR");
            error.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    // 로그인
    // 로그인 성공 시 로그인된 사용자 객체를 return해준다,
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequestDto loginRequest) {
        try{
            UserResponseDto user = userService.login(loginRequest);
            return ResponseEntity.ok(user);
        } catch (IllegalArgumentException e){
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "ILLEGAL ARGUMENT ERROR");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
        catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "INTERNAL SERVER ERROR");
            error.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    // 회원 탈퇴
    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody @Valid DeleteRequestDto deleteRequest) {
        try {
            userService.delete(deleteRequest);
            return ResponseEntity.ok("성공적으로 탈퇴가 완료되었습니다!");
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "ILLEGAL ARGUMENT ERROR");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "INTERNAL SERVER ERROR");
            error.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }

    }


}
