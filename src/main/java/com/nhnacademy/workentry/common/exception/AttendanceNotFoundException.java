package com.nhnacademy.workentry.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class AttendanceNotFoundException extends AttendanceException {
    public AttendanceNotFoundException(Long mbNo) {
        super("출근 기록이 존재하지 않습니다. mbNo : "+ mbNo);
    }
}
