package com.nhnacademy.workentry.controller;

import com.nhnacademy.workentry.attendance.controller.AttendanceController;
import com.nhnacademy.workentry.attendance.dto.AttendanceDto;
import com.nhnacademy.workentry.attendance.dto.AttendanceSummaryDto;
import com.nhnacademy.workentry.attendance.service.AttendanceService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

/**
 * AttendanceController의 단위 테스트 클래스입니다.
 * 출결 관련 REST API의 컨트롤러 계층 동작을 검증합니다.
 */
@WebMvcTest(controllers = AttendanceController.class)
class AttendanceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AttendanceService attendanceService;

    /**
     * 회원의 기간별 출결 데이터를 조회하는 API 테스트입니다.
     * 해당 테스트는 특정 회원 번호와 날짜 범위를 포함한 GET 요청이 성공적으로 200 OK를 반환하는지를 검증합니다.
     */
    @Test
    @DisplayName("1. 회원 출결 내역 조회 성공")
    void testGetAttendanceByNo() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        Page<AttendanceDto> mockPage = new PageImpl<>(List.of());

        Mockito.when(attendanceService.getAttendanceByNoAndDateRange(
                eq(1L),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                any(Pageable.class)
        )).thenReturn(mockPage);

        mockMvc.perform(get("/api/v1/attendances/1")
                        .param("start", "2024-04-01T00:00:00")
                        .param("end", "2024-04-30T23:59:59")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    /**
     * 최근 30일 간 전체 출결 요약 데이터를 조회하는 API 테스트입니다.
     * 페이지 정보 없이 기본값으로 조회하는 경우를 포함하며, 200 OK 상태 반환을 확인합니다.
     */
    @Test
    @DisplayName("2. 전체 출결 요약 조회 성공")
    void testGetRecentAttendanceSummary() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        Page<AttendanceDto> mockPage = new PageImpl<>(List.of());

        Mockito.when(attendanceService.getRecentAttendanceSummary(any(Pageable.class)))
                .thenReturn(mockPage);

        mockMvc.perform(get("/api/v1/attendances/summary/recent")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    /**
     * 특정 회원의 최근 30일 근무 통계 데이터를 조회하는 API 테스트입니다.
     * 회원 번호를 포함한 요청이 정상적으로 처리되어 200 OK를 반환하는지 검증합니다.
     */
    @Test
    @DisplayName("3. 특정 회원 근무 통계 조회 성공")
    void testGetRecentWorkingHoursByMember() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        Page<AttendanceSummaryDto> mockPage = new PageImpl<>(List.of());

        Mockito.when(attendanceService.getRecentWorkingHoursByMember(eq(1L), any(Pageable.class)))
                .thenReturn(mockPage);

        mockMvc.perform(get("/api/v1/attendances/summary/recent/1")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }
}
