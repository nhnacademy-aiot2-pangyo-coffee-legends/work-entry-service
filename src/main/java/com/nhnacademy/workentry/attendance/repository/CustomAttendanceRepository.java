package com.nhnacademy.workentry.attendance.repository;

import com.nhnacademy.workentry.attendance.dto.AttendanceDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

/**
 * QueryDSL 기반 커스텀 쿼리 인터페이스입니다.
 */
public interface CustomAttendanceRepository {

    /**
     * 특정 회원의 지정된 기간 내 출결 기록을 페이지 단위로 조회합니다.
     *
     * @return 페이지 형태의 출결 DTO 목록
     */
    Page<AttendanceDto> getAttendanceByNoAndDateRange(Long no, LocalDateTime start, LocalDateTime end, Pageable pageable);

}
