package com.nhnacademy.workentry.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 특정 회원의 출근 기록이 존재하지 않을 때 발생하는 예외입니다.
 * <p>
 * 이 예외는 출결 데이터를 조회했으나, 해당 기간 또는 조건에 맞는 출근 기록이
 * 존재하지 않을 경우 발생합니다.
 * </p>
 *
 * <p>
 * 컨트롤러에서 발생 시 HTTP 404(NOT_FOUND) 상태 코드로 매핑됩니다.
 * </p>
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class AttendanceNotFoundException extends AttendanceException {
    public AttendanceNotFoundException(Long mbNo) {
        super("출근 기록이 존재하지 않습니다. mbNo : "+ mbNo);
    }
}
