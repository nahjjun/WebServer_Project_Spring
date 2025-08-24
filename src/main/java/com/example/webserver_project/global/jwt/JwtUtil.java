package com.example.webserver_project.global.jwt;

// JWT를 생성하고 검증하는 기능을 하는 클래스

import com.example.webserver_project.domain.user.Dto.JwtUserInfoDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

@Slf4j // lombok이 slf4j 로거 필드를 자동 생성해주는 어노테이션
@Component
public class JwtUtil {
    private final Key key; // SecretKey를 담고 있는 객체
    private final long accessTokenExpTime; // 토큰 만료까지 남은 시간(초) 

    // application.yml에 저장되어있는 secret key와 만료 시간을 @Value로 값에 할당해준다.
    public JwtUtil(
        @Value("${jwt.secret}") final String secretKey,
        @Value("${jwt.expiration_time}") final long accessTokenExpTime
    ) {
        // 내가 설정한 Base64 형식의 secretKey를 BASE64 방식으로 디코딩한다.
        // 이후, 해당 값을 HMAC SHA 알고리즘을 사용한 키로 변환한다.
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);  // 위 줄에서 만든 byte 배열로 secretKey를 만들어준다
            // Keys.hmacShaKeyFor() : 갖고 있는 바이트 배열로 SecretKey를 만들어주는 함수
            // 키 길이가 너무 짧으면 WeakKeyException이 발생함(HS256은 최소 256bit/32byte 권장됨)
        this.accessTokenExpTime = accessTokenExpTime; // 
    }


    // 인증된 사용자 정보를 담은 Dto를 받아 Access Token을 생성하는 함수
    public String createAccessToken(JwtUserInfoDto user){
        return createToken(user, accessTokenExpTime);
    }

    // JWT를 생성하는 내부 함수
    // JWT 생성 과정
        // 1. 클레임(Claims) 생성 & 채우기 -> 토큰 본문(Payload) 데이터
        // 2. 발급/만료 시간 세팅 -> 표즌 등록 클레임 (iat, exp)
        // 3. 서명(HS256 + 시크릿 키)
        // 4. 직렬화 -> header.payload.signature 문자열로 반환
    private String createToken(JwtUserInfoDto user, long expireTime){
        // 1. 클레임(Claims) 생성 & 채우기 -> 토큰 본문(Payload) 데이터
            // Claims 객체 생성 (JJWT의 DefaultClaims)
            // JWT의 Payload에 들어갈 키-값 저장소임
        Claims claims = Jwts.claims().build();
        claims.put("userId", user.getUserId());
        claims.put("email", user.getEmail());
        claims.put("name", user.getName());
        claims.put("role", user.getRole().name()); // enum의 .name() 함수 : 해당 열거형 상수가 선언될 때의 정확한 이름 문자열을 반환함
        // ㄴ> role이 Enum이면 직렬화 시 toString()결과가 들어간다. user.getRole().name()으로 명시하는 것이 좋다


        // 2. 발급/만료 시간 세팅 -> 표즌 등록 클레임 (iat, exp)
        // 현재 시간에 토큰 유효 시간인 expireTime을 더한 ZonedDateTime 객체 생성
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime tokenValidity = now.plusSeconds(expireTime);

        // 3. 서명 생성 및 직렬화
        return Jwts.builder()
                .setClaims(claims) // 위에서 채운 커스텀 클레임 전부를 Payload에 설정하는 함수 (이미 들어있는 표준 클레임(exp, iat 등)을 덮어쓸 수도 있으니 주의해야함)
                .setIssuedAt(Date.from(now.toInstant())) // 표준 등록 클레임 iat(발급 시각, Issued At) 설정
                .setExpiration(Date.from(tokenValidity.toInstant())) // 표준 등록 클레임 exp(토큰 만료 시간, Expiration) 설정
                .signWith(key, SignatureAlgorithm.HS256) // secret key와 알고리즘으로 서명을 수행하는 함수(HMAC-SHA256 알고리즘)
                // ㄴ> 헤더 자동 포함됨 ({"alg":"HS256, "typ":"JWT"})
                .compact(); // 최종적으로 Base64Url(header).Base64Url(payload).Base64Url(signature) 형태의 JWT 문자열 생성
    }



    // JWT Token에서 User ID를 추출하는 함수
    public Long getUserId(String token){
        return parseClaims(token).get("userId", Long.class);
    }

    // JWT Token에서 Claims를 추출하는 함수
    // Jwts : JWT를 다루기 위한 유틸리티 클래스
    public Claims parseClaims(String accessToken){
        return Jwts.parser() // parser() : JWT를 파싱할 준비 객체(JwtParserBuilder)를 반환하는 함수
                             // JwtParserBuilder : 빌더 패턴을 사용해서 parser의 옵션들(서명키, Clock, 압축 설정 등)을 지정할 수 있는 객체
                .setSigningKey(key) // setSigningKey(): 토큰 서명을 검증하기 위한 비밀 키를 설정하는 함수.(서명 검증 완료 시, JwtParserBuilder 반환)
                .build() // 위에서 지금까지 설정한 옵션들을 기반으로 실제 JwtParser 객체를 생성한다.
                         // JwtParser : JWT 문자열을 실제로 parsing하고, 클레임 검증 등을 수행할 수 있는 객체
                .parseClaimsJws(accessToken) // parseClaimsJws(): 전달된 문자열인 accessToken을 JWS(Signed JWT)로 해석하는 함수
                                             // 내부적으로 Signature를 검증하고, Payload를 Claims로 변환해준다.(반환값 Jws<Claims>)
                                             // JWS : Header.Payload.Signature 형태의 서명된 JWT를 뜻함
                .getBody(); // Jws<Claims> 객체에서 Payload만 꺼내오는 함수 (Claims return)
    }


    // JWT 검증하는 함수
    // token으로부터 Jws<Claims> 객체 가져온다. 만약 해당 과정이 실패하면 null을 반환하는게 아니라 예외가 발생하므로, 아래 코드의 if문은 필요 없다.
    // JWT는 Filter 과정에서 예외 처리를 끝낸다. 전역 예외 처리로 처리하지 않는다.
    public boolean isValidToken(String token) {
        Jws<Claims> c = Jwts.parser().setSigningKey(key).build().parseClaimsJws(token);
        // if (c == null) return false;
        return true;
    }


}
