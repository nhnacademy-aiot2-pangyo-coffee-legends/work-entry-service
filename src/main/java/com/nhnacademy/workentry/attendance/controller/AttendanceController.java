package com.nhnacademy.workentry.attendance.controller;

import com.nhnacademy.workentry.attendance.dto.AttendanceDto;
import com.nhnacademy.workentry.attendance.dto.AttendanceSummaryDto;
import com.nhnacademy.workentry.attendance.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 출결 관련 REST API 요청을 처리하는 컨트롤러 클래스입니다.
 * <p>
 * 회원의 출결 정보 및 근무 시간 통계를 조회하는 기능을 제공합니다.
 * </p>
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/attendances")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    /**
     * 회원 번호와 기간을 기준으로 출결 데이터를 조회합니다.
     *
     * @param no    회원 고유 번호
     * @param start 조회 시작 일시 (ISO-8601 형식)
     * @param end   조회 종료 일시 (ISO-8601 형식)
     * @return 해당 기간 동안의 출결 정보 리스트
     */
    @GetMapping("/{no}")
    public List<AttendanceDto> getAttendanceByNo(
            @PathVariable Long no,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {

        log.info("🔍 회원 {}의 출결 조회 요청: {} ~ {}", no, start, end);
        List<AttendanceDto> result = attendanceService.getAttendanceByNoAndDateRange(no, start, end);
        log.debug("조회된 출결 수: {}", result.size());

        return result;
    }

    /**
     * 전체 회원의 최근 30일 출결 정보를 요약하여 조회합니다.
     *
     * @return 최근 30일간의 출결 정보 리스트
     */
    @GetMapping("/summary/recent")
    public List<AttendanceDto> getRecentAttendanceSummary() {
        log.info("📊 최근 30일 출결 요약 요청");
        List<AttendanceDto> result = attendanceService.getRecentAttendanceSummary();
        log.debug("요약된 출결 수: {}", result.size());

        return result;
    }

    /**
     * 특정 회원의 최근 30일간 근무시간 통계를 조회합니다.
     *
     * @param no 회원 고유 번호
     * @return 해당 회원의 날짜별 근무시간 통계 리스트
     */
    @GetMapping("/summary/recent/{no}")
    public List<AttendanceSummaryDto> getRecentWorkingHoursByMember(@PathVariable Long no) {
        log.info("📊 회원 {} 최근 30일 근무 통계 조회 요청", no);
        List<AttendanceSummaryDto> result = attendanceService.getRecentWorkingHoursByMember(no);
        log.debug("조회된 통계 수: {}", result.size());

        return result;
    }

}
