package com.nhnacademy.workentry.attendance.service;

import com.nhnacademy.workentry.attendance.dto.AttendanceDto;

import java.time.LocalDateTime;
import java.util.List;

public interface AttendanceService {

    /**
     * 특정 회원의 전체 출결 내역 조회
     *
     * @param no 회원 번호
     * @return 출결 응답 DTO 리스트
     */
    List<AttendanceDto> getAttendanceByNo(Long no);

    /**
     * 특정 회원의 기간별 출결 기록을 조회합니다.
     *
     * @param no 회원 번호
     * @param start 시작 날짜
     * @param end 종료 날짜
     * @return 출결 DTO 목록
     */
    List<AttendanceDto> getAttendanceByNoAndDateRange(Long no, LocalDateTime start, LocalDateTime end);

    /**
     * 전체 회원의 최근 7일간 출결 현황을 요약합니다.
     *
     * @return 출결 DTO 목록
     */
    List<AttendanceDto> getRecentAttendanceSummary();
}
