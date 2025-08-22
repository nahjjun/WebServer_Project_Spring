package com.example.webserver_project.domain.user.Dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDto {
    @Email(message = "이메일 형식에 맞지 않습니다.")
    @NotNull(message = "이메일 입력은 필수입니다.")
    private String email;

    @NotNull(message = "비밀번호 입력은 필수입니다.")
    private String password;
}
// ㄴ> 로그인용 DTO에는 RoleType을 넣지 않아야한다.
// 해당 권한은 클라이언트가 보내는 값이 아니라, 서버가 DB에서 조회해서 결정해야 한다.
// 로그인 DTO에 Role을 넣게 되면, 클라이언트가 Role에 "ADMIN"을 넣어 보내면 권한 상승 시도가 가능해진다.
// 이는 보안상 위험성이 있기 때문에, 서버는 항상 사용자 정보(이메일, 비밀번호)를 확인한 뒤, DB의 RoleType을 읽어서 권한을 부여해야한다.

// 1. 회원가입
    // 클라이언트로부터 role을 받더라도, 보통은 서버가 강제로 기본값(USER)으로 설정하거나,
    // 관리 화면/승인 로직으로만 ADMIN/MANAGER를 부여한다.
// 2. 로그인
    // email, password만 받는다
    // 서버가 사용자 조회 -> DB의 RoleType을 로드 -> JWT 발급 시 roles 클레임에 넣는다
// 3. 요청 처리
    // 이후 요청 헤더의 JWT에서 roles를 읽어서 Spring Security가 인가 처리함