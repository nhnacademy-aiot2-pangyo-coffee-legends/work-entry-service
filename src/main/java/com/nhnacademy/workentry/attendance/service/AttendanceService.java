package com.nhnacademy.workentry.attendance.service;

import com.nhnacademy.workentry.attendance.dto.AttendanceDto;
import com.nhnacademy.workentry.attendance.dto.AttendanceRequest;
import com.nhnacademy.workentry.attendance.dto.AttendanceSummaryDto;
import com.nhnacademy.workentry.attendance.entity.Attendance;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 출결 서비스 인터페이스입니다.
 * <p>
 * 회원 출결 정보 조회 및 통계 기능을 정의합니다.
 * </p>
 */
public interface AttendanceService {

    /**
     * 특정 회원의 전체 출결 내역을 조회합니다.
     *
     * @param no 회원 번호
     * @return 해당 회원의 전체 출결 정보를 담은 DTO 리스트
     */
    List<AttendanceDto> getAttendanceByNo(Long no);

    /**
     * 특정 회원의 지정된 기간 내 출결 내역을 조회합니다.
     *
     * @param no    회원 번호
     * @param start 시작 날짜 및 시간
     * @param end   종료 날짜 및 시간
     * @return 기간 내 출결 정보를 담은 DTO 리스트
     */
    List<AttendanceDto> getAttendanceByNoAndDateRange(Long no, LocalDateTime start, LocalDateTime end);

    /**
     * 전체 회원의 최근 7일간 출결 현황을 요약합니다.
     *
     * @return 최근 일주일간의 출결 요약 정보를 담은 DTO 리스트
     */
    List<AttendanceDto> getRecentAttendanceSummary();

    /**
     * 특정 회원의 최근 30일간 근무 통계를 조회합니다.
     *
     * @param no 회원 번호
     * @return 날짜별 근무 시간 및 상태 요약 정보를 담은 DTO 리스트
     */
    List<AttendanceSummaryDto> getRecentWorkingHoursByMember(Long no);

    void createAttendance(AttendanceRequest attendanceRequest);

    void checkOut(Long mbNo, LocalDate workDate, LocalDateTime checkOutTime, String status);
}
