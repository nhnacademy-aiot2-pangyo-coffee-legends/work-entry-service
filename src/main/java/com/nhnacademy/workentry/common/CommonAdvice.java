package com.nhnacademy.workentry.common;

import com.nhnacademy.workentry.common.exception.AttendanceException;
import jakarta.ws.rs.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * 컨트롤러 실행할 때 나타날 수 있는 exception이라 앞으로도 controller를 사용하지 않는다면 파일 삭제
 */
@ControllerAdvice
public class CommonAdvice{

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Object> handleNotFound(NotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(AttendanceException.class)
    public ResponseEntity<Object> handleAttendanceException(AttendanceException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
}
