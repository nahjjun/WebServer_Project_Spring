package com.example.webserver_project.global.security;

import com.example.webserver_project.global.jwt.JwtProvider;
import com.example.webserver_project.infra.redis.RedisUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    // 사용자 username(email)로 UserDetails 객체를 가져오기 위해 사용하는 UserDetailsService
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtProvider jwtProvider; // jwt 인증 등의 작업을 하기 위한 jwtUtil 객체
    private final RedisUtil redisUtil;

    // JWT 검증 필터 수행
    // JwtAuthFilter 자체적으로 사용자를 검증한다
    @Override
    protected void doFilterInternal(final HttpServletRequest request,
                                    final HttpServletResponse response,
                                    final FilterChain filterChain) throws ServletException, IOException{
        // 1. HTTP 요청 헤더에서 Authorization 헤더 값을 추출한다
        // Authorization에는 인증된 사용자의 정보가 담겨있다.
            // tokenType : 토큰 타입 지정 - 클라이언트가 인증 헤더를 보낼 때 어떤 scheme를 쓸지 알려주는 값
                // ㄴ> 보통 "Bearer" 사용
            // accessToken : Header.Payload.Signature 구조의 JWT 토큰
            // expiresIn : 토큰 만료 시간까지 남은 시간
            // user : 사용자 정보
        String authorizationHeader = request.getHeader("Authorization");

        // 2. JWT 존재 여부 확인 :
        // Authorization 헤더가 존재하고 "Bearer"로 시작하는지 확인
        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")){
            String token = authorizationHeader.substring(7);
                // ㄴ> 클라이언트가 요청할 때, Authorization 헤더는 아래처럼 온다
                //      "Authorization: Bearer eyJhbGci..."
                // "Bearer " 부분은 인증 스키마이기 때문에, 해당 부분을 자르고 순수한 JWT 문자열을 구하기 위해 substring을 하는 것

            // 3. JWT 유효성 검증
            if(jwtProvider.validateToken(token)){  // access token에서 Jws<Claims> 객체로부터 Claims를 가져옴으로써 유효성을 검증하는 함수
                // 4. JWT 토큰의 유효성이 검증되었다면, 해당 JWT access 토큰의 jti(jwt id)가 Redis의 블랙리스트에 있는지 확인한다.
                // jwtProvider를 이용하여 해당 토큰의 jti를 얻어온 뒤, 해당 jti로 redis에 있는 블랙리스트로 등록되어있는지 여부를 확인한다.
                String jtiKey = RedisUtil.BLACKLIST_TOKEN_PREFIX + jwtProvider.getTokenId(token);
                if(redisUtil.isBlacklisted(jtiKey)){
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Access Token is blacklisted");
                        // 해당 토큰이 블랙리스트인 경우, response로 Error를 전송한다.
                    return;
                }

                // access token이 정상적인 토큰이라면, 해당 토큰의 Claims 객체에서 사용자 아이디를 가져온다
                Long userId = jwtProvider.getUserId(token);

                // 사용자와 토큰이 일치할 시, email로 userDetails 객체 생성
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(String.valueOf(userId));

                // 성공적으로 userDetails를 만들었다면, 접근 권한 인증용 Token을 생성한다.
                // UsernamePasswordAuthenticationToken은 Authentication 구현체이다.
                // 생성된 Authentication 객체를 SecurityContextManger를 사용하여 SecurityContext에 인증 정보를 저장하는 것이다.
                if(userDetails != null){
                    // UserDetails, Password, Role을 이용하여 "접근 권한 인증 Token (Authentication 객체)" 생성
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        // ㄴ> 인자 1 : Object principal => 로그인한 사용자 객체(보통 UserDetails 타입)
                                                     // => SecurityContextHolder.getContext().getAuthentication().getPrincipal() 로 꺼낼 수 있음
                        // ㄴ> 인자 2 : credentials => 사용자가 제출한 자격 증명 (보통 password)
                                                // => 로그인 시도 단계에서는 실제 비밀번호가 들어간다. 하지만, 현재 JWT는 이미 인증된 상태라서 비번을 들 필요가 없으므로 null처리
                        // ㄴ> 인자 3 : authorities => 사용자가 가진 권한(role) 목록
                                                // => userDetails.getAuthorities()로 권한 목록 가져옴

                    // 현재 Request의 Security Context에 접근 권한 설정함
                    SecurityContextHolder.getContext()
                            .setAuthentication(authenticationToken);
                }
            }
        }

        // filterChain의 doFilter() 함수로 다음 필터로 request와 response를 넘긴다
        filterChain.doFilter(request, response);
    }
}
