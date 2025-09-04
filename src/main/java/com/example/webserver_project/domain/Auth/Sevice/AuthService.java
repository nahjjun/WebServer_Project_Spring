package com.example.webserver_project.domain.Auth.Sevice;

import com.example.webserver_project.domain.Auth.Dto.request.LoginRequestDto;
import com.example.webserver_project.domain.Auth.Dto.response.LoginResponseDto;
import com.example.webserver_project.domain.Auth.exception.AuthErrorCode;
import com.example.webserver_project.global.exception.CustomException;
import com.example.webserver_project.global.security.CustomUserDetails;
import com.example.webserver_project.infra.redis.RedisUtil;
import com.example.webserver_project.global.jwt.JwtProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final RedisUtil redisUtil; // refresh 토큰을 저장할 redis 객체
    private final JwtProvider jwtProvider;
    private final AuthenticationManager authenticationManager; // 인증 처리의 진입점이다. 여러 AuthenticationProvider에게 인증을 시도함

    private boolean secure;

    // 로그인 진행 함수 - login 시, 새로운 access token을 발급해주는 함수
    /*
    @param loginRequestDto : 사용자 로그인 요청 담긴 객체
    @param response : access token와 refresh token 헤더를 담기 위한 http response 객체
    @return : 로그인한 사용자 정보가 담긴 LoginResponseDto return
     */
    public LoginResponseDto login(LoginRequestDto loginRequestDto, HttpServletResponse response){
        CustomUserDetails principal = validateUser(loginRequestDto); // 사용자 인증 후, 해당 authentication의 principal 받아오기
        setRefreshTokenToRedisAndSetResponse(principal, response); // redis에 해당 사용자의 refreshToken을 저장한다.

        // 사용자 정보를 LoginResponseDto에 담아서 return
        return LoginResponseDto.builder()
                .id(principal.getUser().getUserId())
                .name(principal.getUser().getName())
                .email(principal.getUser().getEmail())
                .build();
    }



    /*
    - 토큰 재발급 함수 - Refresh 토큰으로 새 Access(+회전된 Refresh) 발급
    - 쿠키에서 refresh token을 추출한 후 redis에 저장된 토큰과 비교하여 유효성을 검증한다.
    - 검증에 성공하면 새로운 액세스 토큰을 생성하여 응답 헤더에 포함시킨다.
    refresh 토큰 회전 : 액세스 토큰을 갱신할 때마다 기존 리프레시 토큰을 무효화하고 새로운 refresh 토큰을 발급하는 보안 강화 메커니즘

    @param request : Http 요청 객체 (쿠키에서 refresh token 추출용)
    @param response : Http 응답 객체 (새로운 액세스 토큰 설정용)
     */
    @Transactional
    public void refresh(HttpServletRequest request, HttpServletResponse response) {
        // 1. 쿠키에서 refreshToken 추출
        String refreshToken = getRefreshTokenFromCookie(request);
        if(refreshToken == null || !jwtProvider.validateToken(refreshToken)) {
            // refreshToken이 null이거나 유효한 refreshToken이 아니면 예외처리
            // refreshToken이 만료된 상태라면, 재로그인을 시킨다.
            throw new CustomException(AuthErrorCode.INVALID_REFRESH_TOKEN);
        }

        // 2. 사용자 ID 추출
        Long userId = jwtProvider.getUserId(refreshToken);

        // 3. redis에 저장된 refresh Token과 쿠키에서 추출한 refresh Token 비교
        String storedToken = redisUtil.getData(RedisUtil.REFRESH_TOKEN_PREFIX + userId);
            // ㄴ> RedisUtil.REFRESH_TOKEN_PREFIX + userId 형식으로 된 key로 저장해둔 토큰을 확인한다.
        if(storedToken != null || !refreshToken.equals(storedToken)) {
            // refresh Token과 stored Token이 같지 않다면, 예외 발생
            throw new CustomException(AuthErrorCode.INVALID_REFRESH_TOKEN);
        }

        // 4. New accessToken, refreshToken 발급 (refresh 토큰 회전)
        String newAccessToken = jwtProvider.createAccessToken(userId);
        String newRefreshToken = jwtProvider.createRefreshToken(userId);
        long exp

        setAccessTokenHeader(response, newAccessToken);
    }



    /*
    - 쿠키에서 refreshToken을 추출하는 함수
    @param request : http 요청 객체
    @return String : refreshToken
     */
    private String getRefreshTokenFromCookie(HttpServletRequest request){
        if(request.getCookies() == null) return null; // 요청에 쿠키가 없으면

        for(Cookie cookie : request.getCookies()) {
            // 쿠키에 refreshToken이 있다면, 해당 쿠키값 반환
            if("refreshToken".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }


    // 사용자 정보를 인증하는 함수 (이메일, 비밀번호 검증)
    /*
        @params loginRequestDto : 로그인 요청 정보 담겨있는 dto
        @return : 사용자 이메일 & 비밀번호로 authenticaitonManager를 사용하여 인증한다.
                   -> 인증 성공 시 authentication 객체를 반환한다.
                   -> 해당 객체에서 사용자 인증 정보가 담겨있는 Principal 객체(UserDetails 구현 객체)를 꺼내온다
     */
    private CustomUserDetails validateUser(LoginRequestDto loginRequestDto){
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
        return principal;
    }


    // redis에 refreshToken을 등록하고, response에 accessToken과 refreshToken Header를 설정하는 등, response 설정 함수
    /*
        @param principal : CustomUserDetails 객체로, Authentication의 principal인 UserDetails 구현 객체이다.
        @param response : Http 응답 객체
     */
    private void setRefreshTokenToRedisAndSetResponse(CustomUserDetails principal, HttpServletResponse response){
        // 해당 principal에서 userId를 얻어온다
        Long userId = principal.getUser().getUserId();

        // 얻어온 userId로 AccessToken, RefreshToken을 생성한다.
        String accessToken = jwtProvider.createAccessToken(userId);
        String refreshToken = jwtProvider.createRefreshToken(userId);

        // redis에 refreshToken 저장하기
        // key-userId & value-refreshToken
        redisUtil.setData(String.valueOf(userId), refreshToken, jwtProvider.getExpirationMiliSecond(refreshToken));

        // response Access Token Header 설정
        setAccessTokenHeader(response, accessToken);
        // response에 Refresh Token Header 설정
        setRefreshTokenHeader(response, refreshToken, jwtProvider.getExpirationMiliSecond(refreshToken)/1000);

    }

    // response Header에 accessToken 헤더 설정하는 함수
    private void setAccessTokenHeader(HttpServletResponse response, String accessToken){
        response.setHeader("Authorization",  "Bearer " + accessToken);
    }

    // response Header에 refreshToken 헤더 설정하는 함수
    /*
    @param response, refreshToken : http 응답, 리프레시 토큰
    @param expireSeconds : refreshToken 만료 초
     */
    private void setRefreshTokenHeader(HttpServletResponse response, String refreshToken, long expireSeconds){
        // ResponseCookie : 스프링에 제공하는 쿠키 빌더.
        // ResponseCookie.from(name, value) : ResponseCookieBuilder 반환
            // ㄴ> name : 쿠키의 이름 / value : 쿠키의 값
            // 브라우저에 name:<value> 형태의 쿠키가 저장되도록 Set-Cookie 헤더를 만드는 것임
        ResponseCookie.ResponseCookieBuilder cookie =
                ResponseCookie.from("refreshToken", refreshToken)
                        .httpOnly(true) // 해당 쿠키에 JS(문서, 콘솔)의 document.cookie 식의 접근을 차단함 -> XSS 방어에 핵심임 (악의적인 스크립트가 쿠키 정보를 탈취하는 것을 막음)
                        .path("/") // 어떤 경로의 요청에 쿠키가 담길지 범위를 지정함 (전 사이트 경로에서 전송. 보안 강화하려면 /auth 등의 url로 좁힐 수 있음)
                        .secure(false) // 로컬은 HTTP이므로 false 지정. HTTPS(운영)일 때는 true로 설정한다
                        .sameSite("Lax") //
                        .maxAge(Duration.ofSeconds(expireSeconds)); // 쿠키 유효시간 설정(refresh Token의 TTL와 동기화함. Duration.ZERO를 주면 즉시 만료(삭제) 쿠키로 사용 가능함)

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.build().toString()); // cookiebuilder 객체를 build해서 String으로 바꾼 뒤, response의 헤더로 등록시킨다.
    }
}
