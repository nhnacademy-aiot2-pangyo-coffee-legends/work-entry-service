package com.nhnacademy.workentry.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AttendanceStatusNotFoundException extends RuntimeException {
    public AttendanceStatusNotFoundException(String statusDescription) {
        super("출결 상태가 존재하지 않습니다. statusDescription : " + statusDescription);
    }
}
