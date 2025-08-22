package com.example.webserver_project.domain.user.Dto;

import com.example.webserver_project.global.jwt.RoleType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class JoinRequestDto {
    // 해당 message는 @RequestBody를 붙인 인자들의 두번째 인자로 BindingResult 지정해서 컨트롤러에서 직접 메시지를 꺼낼 수 있다.
    // 하지만, BindingResult는 Form 방식(@ModelAttribute)에서는 쓰이지만 API 호출 방식(@RequestBody, JSON)에서는 잘 쓰이지 않는다.

    // REST API 방식에서는 해당 메시지는 MethodArgumentNotValidException으로 던져지며, @RestControllerAdvice + @ExcpetionHandler에서 잡아서 JSON 응답으로 내려줄 수 있다.
    @NotNull(message = "이름은 필수 입력 값입니다.")
    private String name;

    @NotNull(message = "email은 필수 입력 값입니다.")
    @Email(message = "이메일 형식에 맞지 않습니다.")
    private String email;

    @NotNull(message = "비밀번호는 필수 입력 값입니다.")
    private String password;

    private RoleType role;
}
