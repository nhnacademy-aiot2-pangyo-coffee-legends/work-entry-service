package com.nhnacademy.workentry.common.exception;

import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 회원 서비스 호출 중 예기치 않은 오류가 발생했을 때 사용되는 예외 클래스입니다.
 *
 * <p>Feign 클라이언트를 통해 외부 회원 서비스 호출 중 네트워크 오류, 응답 실패 등
 * 내부 서버 오류(500)를 의미하는 상황에서 이 예외가 발생합니다.</p>
 *
 * <p>이 예외가 발생하면 클라이언트에게 HTTP 500 (Internal Server Error) 상태 코드가 반환됩니다.</p>
 */
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class MemberCallException extends RuntimeException {
    public MemberCallException(String message, FeignException e) {
        super(message, e);
    }
}
