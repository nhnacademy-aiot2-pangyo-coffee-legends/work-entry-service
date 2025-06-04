package com.nhnacademy.workentry.common;

import com.nhnacademy.workentry.common.exception.AttendanceException;
import jakarta.ws.rs.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * 전역 예외 처리 컨트롤러 어드바이스 클래스입니다.
 * <p>
 * 애플리케이션 전역에서 발생하는 커스텀 예외를 처리하여 일관된 응답을 제공합니다.
 * 주로 출결 관련 예외 및 리소스를 찾을 수 없는 예외를 처리합니다.
 * </p>
 */
@ControllerAdvice
public class CommonAdvice{

    /**
     * 리소스를 찾을 수 없는 경우 발생하는 {@link NotFoundException}을 처리합니다.
     *
     * @param e 발생한 NotFoundException 예외 객체
     * @return HTTP 404 상태 코드와 예외 메시지를 포함한 응답 본문
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Object> handleNotFound(NotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    /**
     * 출결 처리 중 발생하는 {@link AttendanceException} 예외를 처리합니다.
     *
     * @param e 발생한 AttendanceException 예외 객체
     * @return HTTP 404 상태 코드와 예외 메시지를 포함한 응답 본문
     */
    @ExceptionHandler(AttendanceException.class)
    public ResponseEntity<Object> handleAttendanceException(AttendanceException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
}
