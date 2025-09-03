package com.example.webserver_project.domain.Auth.Sevice;

import com.example.webserver_project.domain.Auth.Dto.request.JoinRequestDto;
import com.example.webserver_project.domain.Auth.Dto.request.LoginRequestDto;
import com.example.webserver_project.domain.Auth.Dto.request.RefreshRequestDto;
import com.example.webserver_project.domain.Auth.Dto.response.JoinResponseDto;
import com.example.webserver_project.domain.Auth.Dto.response.RefreshResponseDto;
import com.example.webserver_project.global.security.CustomUserDetails;
import com.example.webserver_project.global.security.JwtUserInfoDto;
import com.example.webserver_project.infra.redis.RedisUtil;
import com.example.webserver_project.domain.user.Entity.User;
import com.example.webserver_project.domain.user.Repository.UserRepository;
import com.example.webserver_project.global.jwt.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private static final String REFRESH_PREFIX


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisUtil redisUtil; // refresh 토큰을 저장할 redis 객체
    private final AuthenticationManager authenticationManager; // 인증 처리의 진입점이다. 여러 AuthenticationProvider에게 인증을 시도함
    private final JwtProvider jwtProvider;

    // 로그인 진행 함수 - login 시, 새로운 access token을 발급해주는 함수
    public String login(LoginRequestDto loginRequestDto){
        // 인증 진입점인 AuthenticationManager를 사용하여
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDto.getEmail(), loginRequestDto.getPassword())
        );
        // ㄴ> 내부적으로 AuthenticationProvider(DaoAuthenticationProvider)
        // -> UserDetailsService.loadUserByUsername(email)로 DB에서 사용자 로드
        // -> PasswordEncoder.matches(raw, encoded)로 비번 검증
        // -> 성공 시 인증 완료 Authentication 반환
        // 위 과정을 통해 로그인 인증 과정을 내부적으로 수행한다. 따라서, UserDetailsService를 구현해놓고 PasswordEncoder 빈 등록을 해주어야 하는 것이다.

        CustomUserDetails principal = (CustomUserDetails) auth.getPrincipal();
        // auth.getPrincipal() : 현재 로그인한 사용자 정보를 가져오는 메서드
        // ㄴ> 현재 사용자 정보는 UserDetails를 상속한 CustomUserDetails이다.

        // 해당 principal에서 userId를 얻어온다
        Long userId = principal.getUser().getUserId();

        // 얻어온 userId로 AccessToken, RefreshToken을 생성한다.
        String accessToken = jwtProvider.createAccessToken(userId);
        String refreshToken = jwtProvider.createRefreshToken(userId);

        // ms 초 단위로 변환
        long refreshTokenExpireSecond = jwtProvider.getExpiration(refreshToken).getTime() / 1000;
        redisUtil



        // JwtUerInfoDto, jwtUtil를 이용하여 token 생성
        // token claim에 사용자 정보를 담기 때문에, 굳이 TokenResponseDto를 선언할 필요가 없다
        String token = jwtProvider.createAccessToken(principal);
        return token;
    }
    
    
    
    // 회원가입 진행 함수
    @Transactional
    public JoinResponseDto join(JoinRequestDto joinRequestDto) {
        // 1. 회원가입하려는 회원 이메일로 중복확인
        if(userRepository.existsByEmail(joinRequestDto.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다!");
        }

        // 2. 중복이 안된 이메일이라면, 해당 User Entity 객체를 생성해서 repository로 save한다,
        User user = User.from(joinRequestDto);
        // PasswordEncoder로 비밀번호 인코딩
        user.setPassword(passwordEncoder.encode(joinRequestDto.getPassword())); // user 객체에 인코딩된 비밀번호 저장
        User savedUser = userRepository.save(user); // save 성공 시, 인자로 넣은 객체와 동일한 데이터를 갖고 있는 객체를 다시 반환함

        // 3. save 성공 시 ResponseDto에 해당 객체의 데이터 담는다.
        // Service는 비즈니스 로직에만 집중한다.
        return JoinResponseDto.of(user);
    }

    // 토큰 재발급 함수 - Refresh 토큰으로 새 Access(+회전된 Refresh) 발급
    // refresh 토큰 회전 : 액세스 토큰을 갱신할 때마다 기존 리프레시 토큰을 무효화하고 새로운 refresh 토큰을 발급하는 보안 강화 메커니즘
    @Transactional
    public RefreshResponseDto refresh(RefreshRequestDto refreshRequestDto) {
        if(!jwtProvider.validateR)

    }



    private void setAccessTokenHeader(HttpServletResponse response, String accessToken){
        response.setHeader("Authorization",  "Bearer " + accessToken);
    }
}
