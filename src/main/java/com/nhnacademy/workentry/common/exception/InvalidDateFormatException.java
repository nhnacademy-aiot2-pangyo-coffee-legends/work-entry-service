package com.nhnacademy.workentry.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


/**
 * 잘못된 날짜 형식의 입력값이 전달되었을 때 발생하는 예외입니다.
 *
 * <p>예를 들어, 클라이언트가 ISO-8601 형식이 아닌 날짜를 요청 파라미터로 전달하는 경우
 * 이 예외가 발생하며, HTTP 400(Bad Request) 상태 코드로 응답됩니다.</p>
 *
 * <p>이 예외는 주로 {@code @RequestParam}이나 {@code @RequestBody}에서 날짜 파싱이 실패할 때
 * 처리하기 위해 사용됩니다.</p>
 *
 * @see org.springframework.web.bind.annotation.ResponseStatus
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidDateFormatException extends RuntimeException {

    /**
     * 지정된 메시지를 포함하는 InvalidDateFormatException을 생성합니다.
     *
     * @param message 예외 메시지
     */
    public InvalidDateFormatException(String message) {
        super(message);
    }

    /**
     * 지정된 메시지와 원인(cause)을 포함하는 InvalidDateFormatException을 생성합니다.
     *
     * @param message 예외 메시지
     * @param cause 원인 예외
     */
    public InvalidDateFormatException(String message, Throwable cause) {
        super(message, cause);
    }
}
