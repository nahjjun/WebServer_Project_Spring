package com.example.webserver_project.infra.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeUnit;


@Service
@RequiredArgsConstructor

// JWT에서 Redis 방식으로 RefreshToken을 저장해주는 작업을 담당하는 클래스
public class RedisUtil {
    public static final String REFRESH_TOKEN_PREFIX = "refresh token: ";
    public static final String BLACKLIST_TOKEN_PREFIX = "blacklist: ";

    private final StringRedisTemplate redis;
    // ㄴ> StringRedisTemplate : Spring Data Redis에서 제공하는 Redis 접근 도구 클래스




    // key를 이용하여 value 데이터를 가져오는 함수
    public String getData(String key) {
        ValueOperations<String, String> ops = redis.opsForValue();
        return ops.get(key);
    }

    // 데이터 존재 여부 확인 함수
    public boolean existData(String key){
        return Boolean.TRUE.equals(redis.hasKey(key));
    }



    // 데이터 생성 함수
    public void setData(String key, String value){
        ValueOperations<String, String> ops = redis.opsForValue();
        ops.set(key,value);
    }

    // 데이터 생성 및 파기 시간 지정 함수
    public void setData(String key, String value, long ttlSecond){
        ValueOperations<String, String> ops = redis.opsForValue();
        // ValueOperations : Redis의 String 타입(value)을 다루는 API
        // redis.opsForValue() : Redis의 String 타입 조작 함수. value값으로 String값을 조작하도록 하는 ValueOperations를 반환함
            // ㄴ> 타입 잘못 지정하면 오류나기 때문에, 접두사를 붙여서 구분하는 것임

        Duration duration = Duration.ofSeconds(ttlSecond); // 인자로 들어온 초만큼 Duration 객체를 생성함
        ops.set(key, value, duration);
        // ㄴ> set(key, value, timeout, unit) : TTL과 함께 저장. 마지막에는 TimeUnit의 타입 지정
    }
    /* ㄴ> 로그인으로 쓰일 수 있음 (refreshToken 저장)
        key : userId
        value : refreshToken
        ttlMillis : Time To Live 밀리세컨드 값
     */

    // 데이터 제거 함수 (로그아웃에 사용 가능)
    public void deleteData(String key){
        redis.delete(key); // redis.delete(key) : 키를 삭제하는 함수
    }



    /*
    * refresh token 존재하는지 & 만료되지는 않았는지 확인하는 함수
     refresh token을 회전(rotation)할 때는 find()로 확인 -> 새 RT로 교체, 저장해야 함
     탈취나 재사용 공격을 막기 위해서, 저장된 RT와 요청으로 들어온 RT가 일치하는 과정이 반드시 필요함
     즉, find()가 return한 Optional.empty()가 리턴되면 만료됐거나 이미 로그아웃해서 삭제된 상태임
     */
    public Optional<String> refreshTokenValid(String key){
        String v = redis.opsForValue().get(key);
        return Optional.ofNullable(v); // 인자로 준 v(데이터)가 null인지 아닌지 확인하는 함수
    }



    /*
    Access 블랙리스트 처리 함수 - 인자로 들어온 JWT ID를 블랙리스트에 등록시켜주는 함수
    로그아웃된 user의 jti(jwt id)를 블랙리스트에 등록시켜서 토큰 중복을 방지하며, 재사용 공격 방어에 도움을 준다.
    => jti로 블랙리스트 관리를 해야 해당 accessToken 하나만 무효화 하므로 정확도가 높아진다.
        ㄴ> 만약 userId로 블랙리스트를 관리한다면, 해당 userId의 모든 accessToken을 막는 것이다.
    => ttl 관리가 쉬움
    */
    public void setBlacklist(String key, long ttlMillis){
        redis.opsForValue().set(key, "1", ttlMillis, TimeUnit.MILLISECONDS);
        // ㄴ> "bl: " 접두사를 붙여서 블랙리스트 데이터임을 명시한다.
        // value값으로 "1"을 넣어서 해당 key가 블랙리스트에 등록되어있음을 표시한다(boolean)
    }

    // 인자로 들어온 (JWT key) 값이 블랙리스트에 등록되어있는지 확인한다
    public boolean isBlacklisted(String key){
        return redis.hasKey((key));
    }
}
