package com.example.webserver_project.infra.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;


@Service
@RequiredArgsConstructor

// JWT에서 Redis 방식으로 RefreshToken을 저장해주는 작업을 담당하는 클래스
public class RedisUtil implements RefreshTokenStore {
    private final StringRedisTemplate redis;
    // ㄴ> StringRedisTemplate : Spring Data Redis에서 제공하는 Redis 접근 도구 클래스




    /* 로그인 (refreshToken 저장)
        username : 사실상 userId
        refreshToken : refreshToken
        ttlMillis : Time To Live 밀리세컨드 값
     */
    @Override
    public void save(String username, String refreshToken, long ttlMillis){
        ValueOperations<String, String> ops = redis.opsForValue();
        // ValueOperations : Redis의 String 타입(value)을 다루는 API
        // redis.opsForValue() : Redis의 String 타입 조작 함수. value값으로 String값을 조작하도록 하는 ValueOperations를 반환함
            // ㄴ> 타입 잘못 지정하면 오류나기 때문에, 접두사를 붙여서 구분하는 것임
        ops.set(rtKey(username), refreshToken, ttlMillis, TimeUnit.MILLISECONDS);
        // ㄴ> set(key, value, timeout, unit) : TTL과 함께 저장. 마지막에는 TimeUnit의 타입 지정
    }


    /*
    * refresh token 존재하는지 & 만료되지는 않았는지 확인하는 함수
     refresh token을 회전(rotation)할 때는 find()로 확인 -> 새 RT로 교체, 저장해야 함
     탈취나 재사용 공격을 막기 위해서, 저장된 RT와 요청으로 들어온 RT가 일치하는 과정이 반드시 필요함
     즉, find()가 return한 Optional.empty()가 리턴되면 만료됐거나 이미 로그아웃해서 삭제된 상태임
     */
    @Override
    public Optional<String> find(String username){
        String v = redis.opsForValue().get(rtKey(username));
        return Optional.ofNullable(v); // 인자로 준 v(데이터)가 null인지 아닌지 확인하는 함수
    }

    // 로그아웃 함수 (refresh token 제거)
    @Override
    public void delete(String username){
        redis.delete(rtKey(username)); // redis.delete(key) : 키를 삭제하는 함수
    }

    /*
    Access 블랙리스트 처리 함수 - 인자로 들어온 JWT ID를 블랙리스트에 등록시켜주는 함수
    로그아웃된 user의 jti(jwt id)를 블랙리스트에 등록시켜서 토큰 중복을 방지하며, 재사용 공격 방어에 도움을 준다.
    */
    public void setBlacklist(String jwtId, long ttlMillis){
        redis.opsForValue().set(blKey(jwtId), "1", ttlMillis, TimeUnit.MILLISECONDS);
        // ㄴ> "bl: " 접두사를 붙여서 블랙리스트 데이터임을 명시한다.
        // value값으로 "1"을 넣어서 해당 key가 블랙리스트에 등록되어있음을 표시한다(boolean)
    }

    // 인자로 들어온 JWT id 값이 블랙리스트에 등록되어있는지 확인한다
    public boolean isBlacklisted(String jwtId){
        return redis.hasKey(blKey(jwtId));
    }




/*
     Redis는 모든 데이터를 "key-value" 형태로 저장한다.
     따라서, 해당 사용자의 Refresh Token을 redis 안에서 찾으려면 고유한 키 이름을 정해야 한다.
     아래처럼 "refresh token: "를 붙여준 것은 Redis Key 네이밍 규칙을 통일하기 위함이다.
 */
    // refresh token용 key 생성 함수
    private String rtKey(String username){
        return "refresh token: " + username;
        // ㄴ> "refresh token :" = prfix(접두사). 데이터의 성격을 구분하기 위해 붙인 것임. 다른 종류의 데이터에는 다른 접두사를 붙인다.
    }

    // 블랙리스트용 key 생성 함수
    private String blKey(String jti){
        return "blacklist: " + jti;
    }
}
