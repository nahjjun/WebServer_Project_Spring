package com.example.webserver_project.global.response;

import com.example.webserver_project.global.status.SuccessStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({"isSuccess", "code", "message", "result"})
public class GlobalWebResponse<T> {
    @JsonProperty("isSuccess") // 명시적으로 json에서 어떤 name값으로 설정될지 설정하는 어노테이션
    private final Boolean isSuccess;

    // HttpStatus 상태 코드로는 세부적인 오류 사유까지 표현하기 어려움
    // 따라서, "U2001"(회원가입 실패) 등의 세부적인 코드 설정 가능
    @JsonProperty("code")
    private final String code; // 비즈니스 코드
    @JsonProperty("message")
    private final String message; // 세부적인 메시지 전달

    // result값이 null이 아니어야 Json에 포함되도록 설정
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T result;

    // 성공 응답 생성 함수
    // 유형별로 다른 code, 메시지를 받기 위해 인자로 해당 값들 받기
    public static <T>GlobalWebResponse<T> success(String code, String message, T result){
        return new GlobalWebResponse<T>(true, code, message, result);
    }

    // 실패 응답 생성 함수
    public static <T>GlobalWebResponse<T> error(String code, String message){
        return new GlobalWebResponse<T>(false, code, message, null);
    }




}
