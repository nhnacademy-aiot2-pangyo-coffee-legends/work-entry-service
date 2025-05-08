package com.nhnacademy.workentry.attendance.controller;

import com.nhnacademy.workentry.attendance.dto.AttendanceDto;
import com.nhnacademy.workentry.attendance.dto.AttendanceSummaryDto;
import com.nhnacademy.workentry.attendance.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 출결 관련 REST API 요청을 처리하는 컨트롤러 클래스입니다.
 * <p>
 * 출결 데이터 조회 및 페이지네이션 처리를 위한 엔드포인트를 제공합니다.
 * </p>
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/attendances")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    /**
     * 특정 회원의 기간별 출결 내역을 페이지네이션 방식으로 조회합니다.
     *
     * @param no 회원 고유 번호
     * @param start 조회 시작 일시 (ISO-8601 형식)
     * @param end 조회 종료 일시 (ISO-8601 형식)
     * @param pageable 페이지 정보 (page, size 등)
     * @return {@link AttendanceDto} 페이징 결과
     */
    @GetMapping("/{no}")
    public Page<AttendanceDto> getAttendanceByNo(
            @PathVariable Long no,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            @PageableDefault(size = 10) Pageable pageable) {

        log.info("🔍 회원 {}의 출결 조회 요청: {} ~ {}", no, start, end);
        return attendanceService.getAttendanceByNoAndDateRange(no, start, end, pageable);
    }


    /**
     * 전체 출결 요약 데이터를 페이지 단위로 조회합니다.
     *
     * @param pageable 페이지 및 사이즈 정보를 담은 Pageable 객체
     * @return 페이지 형태의 출결 DTO 목록
     */
    @GetMapping("/summary/recent")
    public Page<AttendanceDto> getRecentAttendanceSummary(@PageableDefault(size=10) Pageable pageable) {
        log.info("📊 최근 30일 출결 요약 요청");
        return attendanceService.getRecentAttendanceSummary(pageable);
    }

    /**
     * 특정 회원의 근무 통계를 페이지 단위로 조회합니다.
     *
     * @param no       회원 고유 번호
     * @param pageable 페이지 및 사이즈 정보를 담은 Pageable 객체
     * @return 페이지 형태의 근무 통계 DTO 목록
     */
    @GetMapping("/summary/recent/{no}")
    public Page<AttendanceSummaryDto> getRecentWorkingHoursByMember(@PathVariable Long no,@PageableDefault(size=10) Pageable pageable) {
        log.info("📊 회원 {} 최근 30일 근무 통계 요청", no);
        return attendanceService.getRecentWorkingHoursByMember(no, pageable);
    }
}
