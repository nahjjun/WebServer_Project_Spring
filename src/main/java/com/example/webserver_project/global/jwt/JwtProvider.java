package com.example.webserver_project.global.jwt;

// JWT를 생성하고 검증하는 기능을 하는 클래스

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Slf4j // lombok이 slf4j 로거 필드를 자동 생성해주는 어노테이션
@Component
public class JwtProvider {
    private Key key; // SecretKey를 담고 있는 객체
    private final long accessTokenExpTime; // access 토큰 만료까지 남은 시간(초)
    private final long refreshTokenExpTime; // refresh 토큰 만료까지 남은 시간(초)



    // application.yml에 저장되어있는 secret key와 만료 시간을 @Value로 값에 할당해준다.
    public JwtProvider(
        @Value("${jwt.secret}") final String secretKey,
        @Value("${jwt.access-token-expiration_time}") final long accessTokenExpTime,
        @Value("${jwt.refresh-token-expiration_time") final long refreshTokenExpTime
    ) {
        // 내가 설정한 Base64 형식의 secretKey를 BASE64 방식으로 디코딩한다.
        // 이후, 해당 값을 HMAC SHA 알고리즘을 사용한 키로 변환한다.
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);  // 위 줄에서 만든 byte 배열로 secretKey를 만들어준다
            // Keys.hmacShaKeyFor() : 갖고 있는 바이트 배열로 SecretKey를 만들어주는 함수
            // 키 길이가 너무 짧으면 WeakKeyException이 발생함(HS256은 최소 256bit/32byte 권장됨)
        this.accessTokenExpTime = accessTokenExpTime; //
        this.refreshTokenExpTime = refreshTokenExpTime; //
    }


    // 인증된 사용자 정보를 담은 Dto를 받아 Access Token을 생성하는 함수
    public String createAccessToken(long userId){
        return createToken(userId, accessTokenExpTime);
    }

    public String createRefreshToken(long userId){
        return createToken(userId, refreshTokenExpTime);
    }

    // JWT를 생성하는 내부 함수
    // JWT 생성 과정
        // 1. 클레임(Claims) 생성 & 채우기 -> 토큰 본문(Payload) 데이터
        // 2. 발급/만료 시간 세팅 -> 표즌 등록 클레임 (iat, exp)
        // 3. 서명(HS256 + 시크릿 키)
        // 4. 직렬화 -> header.payload.signature 문자열로 반환
    private String createToken(long userId, long expireTime){
        Date now = new Date(); // 현재 시간 준비. 발급시각(iat), 만료시각(exp) 계산의 기준점이 필요하기 때문임
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                    // 주제(subject) 지정. sub 클레임에 사용자 식별자를 저장한다. 핵심 식별자는 보통 sub에 넣는게 표준임
                    // getSubject()로 쉽게 꺼내기 좋음
                .setId(UUID.randomUUID().toString())
                    // jti 클레임(jwt id)에 전역 고유값을 저장함 (UUID : 범용 고유 식별자)
                .setIssuedAt(now)
                    // 발급 시각(issued at) 지정. 현재임
                .setExpiration(new Date(now.getTime() + expireTime))
                    // 만료 시각(expiration) 지정. 현재 시간에 만료 시간 더해서 지정함
                .signWith(key, SignatureAlgorithm.HS256)
                    // 준비된 대칭키와 서명 알고리즘(HS256)으로 서명
                .compact();
                    // header/claims를 base64url 인코딩하며, signature를 붙여 최종 문자열로 직렬화 하는 함수
    }

    // JWT 검증하는 함수
    // JWT는 Filter 과정에서 예외 처리를 끝낸다. 전역 예외 처리로 처리하지 않는다.
    public boolean validateToken(String token) {
        parseClaims(token); // parseClaims() 함수를 실행하다가 성공하면 현재 함수에서 true가 return되며, 에러가 난다면 중간에 에러가 return될 것이다.
        return true;
        // ㄴ> 향후, 나경이 코드 보고 예외처리 추가하기
    }


    // JWT Token에서 Claims를 추출하는 함수
    // Jwts : JWT를 다루기 위한 유틸리티 클래스
    public Claims parseClaims(String token){
        return Jwts.parser() // parser() : JWT를 파싱할 준비 객체(JwtParserBuilder)를 반환하는 함수
                             // JwtParserBuilder : 빌더 패턴을 사용해서 parser의 옵션들(서명키, Clock, 압축 설정 등)을 지정할 수 있는 객체
                .setSigningKey(key) // setSigningKey(): 토큰 서명을 검증하기 위한 비밀 키를 설정하는 함수.(서명 검증 완료 시, JwtParserBuilder 반환)
                .build() // 위에서 지금까지 설정한 옵션들을 기반으로 실제 JwtParser 객체를 생성한다.
                         // JwtParser : JWT 문자열을 실제로 parsing하고, 클레임 검증 등을 수행할 수 있는 객체
                .parseClaimsJws(token) // parseClaimsJws(): 전달된 문자열인 accessToken을 JWS(Signed JWT)로 해석하는 함수
                                             // 내부적으로 Signature를 검증하고, Payload를 Claims로 변환해준다.(반환값 Jws<Claims>)
                                             // JWS : Header.Payload.Signature 형태의 서명된 JWT를 뜻함
                .getBody(); // Jws<Claims> 객체에서 Payload만 꺼내오는 함수 (Claims return)
    }


    // JWT Token에서 User Id를 추출하는 함수
    public Long getUserId(String token){
        return parseClaims(token).get("id", Long.class);
    }

    // JWT Token에서 Token 전용 Id(jti)를 추출하는 함수
    public String getTokenId(String token){
        return parseClaims(token).getId();
            // ㄴ> Claims.getId() : JWT 표준 클레임 "jti"에 해당하는 값을 꺼내주는 함수
            // createToken()에서 .setId(UUID.randomUUID().toString()) 했을 때 들어가는 값임
            // jti : 토큰 자체의 고유 식별자. 로그아웃할 때 해당 jti를 DB/Redis에 블랙리스트로 저장한다. 이후, 같은 토큰이 들어오면 거부하는 식으로 사용된다.
            // 즉, 중복 방지/재사용 공격 방어에 도움이 된다.
    }

    // JWT Token에서 만료 시간(exp)을 추출하는 함수
    public Date getExpiration(String token){
        return parseClaims(token).getExpiration();
    }
}
