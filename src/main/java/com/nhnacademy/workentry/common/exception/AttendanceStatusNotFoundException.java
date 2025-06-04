package com.nhnacademy.workentry.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 주어진 출결 상태 설명에 해당하는 유효한 출결 상태 코드가 존재하지 않을 때 발생하는 예외입니다.
 * <p>
 * 예를 들어, 클라이언트가 정의되지 않은 출결 상태 문자열을 요청 파라미터 또는 JSON 등으로
 * 전달했을 경우 발생합니다.
 * </p>
 *
 * <p>
 * 이 예외는 HTTP 요청이 잘못된 경우를 의미하므로,
 * {@link HttpStatus#BAD_REQUEST} (400) 상태 코드와 함께 응답됩니다.
 * </p>
 *
 * @see org.springframework.web.bind.annotation.ResponseStatus
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AttendanceStatusNotFoundException extends RuntimeException {
    public AttendanceStatusNotFoundException(String statusDescription) {
        super("출결 상태가 존재하지 않습니다. statusDescription : " + statusDescription);
    }
}
