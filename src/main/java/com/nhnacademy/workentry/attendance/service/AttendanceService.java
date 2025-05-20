package com.nhnacademy.workentry.attendance.service;

import com.nhnacademy.workentry.attendance.dto.AttendanceDto;
import com.nhnacademy.workentry.attendance.dto.AttendanceRequest;
import com.nhnacademy.workentry.attendance.dto.AttendanceSummaryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
     * 특정 회원의 지정된 기간 내 출결 내역을 페이지네이션으로 조회합니다.
     *
     * @param no        회원 고유 번호
     * @param start     시작 날짜 및 시간
     * @param end       종료 날짜 및 시간
     * @param pageable  페이지 번호, 크기 등 페이징 정보
     * @return {@link AttendanceDto} 객체를 담은 페이지 객체
     */
    Page<AttendanceDto> getAttendanceByNoAndDateRange(Long no, LocalDateTime start, LocalDateTime end, Pageable pageable);


    /**
     * 최근 30일 전체 출결 데이터를 페이지 단위로 조회합니다.
     *
     * @param pageable 페이지 정보
     * @return 페이지 형태의 출결 요약 데이터
     */
    Page<AttendanceDto> getRecentAttendanceSummary(Pageable pageable);

    /**
     * 특정 회원의 최근 30일 근무 데이터를  조회합니다.
     *
     * @param no       회원 번호
     * @return 페이지 형태의 근무 통계
     */
    Page<AttendanceSummaryDto> getRecentWorkingHoursByMember(Long no,Pageable pageable);

    List<AttendanceSummaryDto> getRecentWorkingHoursByMember(Long no);

    void createAttendance(AttendanceRequest attendanceRequest);

    void checkOut(Long mbNo, LocalDate workDate);
}
