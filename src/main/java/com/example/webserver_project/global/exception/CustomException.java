package com.example.webserver_project.global.exception;

import com.example.webserver_project.global.exception.model.BaseErrorCode;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
        // ㄴ> RuntimeException은 Unchecked 예외를 처리한다. 따라서 메서드 시그니처에 throws를 강제하지 않는다.
        // 따라서, 해당 클래스를 상속받으면 메소드에 throws를 남발하지 않아도 되서 API가 깔끔해진다.
        // 서비스/도메인 계층에서 throw new CustomException(errorCode)로 예외를 던질 수 있다. (컨트롤러까지 전파됨)
        // 전역 예외 처리기 @RestControllerAdvice에서 일관된 json 응답 만들기 가능함

    private final BaseErrorCode errorCode;

    public CustomException(BaseErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
