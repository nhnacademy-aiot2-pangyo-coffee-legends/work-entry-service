package com.nhnacademy.workentry.common.exception;

public abstract class AttendanceException extends RuntimeException {
    protected AttendanceException(String message) {
        super(message);
    }
}
