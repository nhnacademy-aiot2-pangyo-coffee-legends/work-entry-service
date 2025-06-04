package com.nhnacademy.workentry.common.exception;

import feign.codec.DecodeException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class MemberDataFormatException extends RuntimeException {
    public MemberDataFormatException(String message, DecodeException e) {
        super(message, e);
    }
}
