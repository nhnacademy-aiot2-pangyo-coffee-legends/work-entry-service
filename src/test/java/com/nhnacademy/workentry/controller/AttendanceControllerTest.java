package com.nhnacademy.workentry.controller;

import com.nhnacademy.workentry.attendance.controller.AttendanceController;
import com.nhnacademy.workentry.attendance.dto.AttendanceDto;
import com.nhnacademy.workentry.attendance.dto.AttendanceSummaryDto;
import com.nhnacademy.workentry.attendance.service.AttendanceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * AttendanceController 통합 테스트
 */
@SpringBootTest
@AutoConfigureMockMvc
class AttendanceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AttendanceService attendanceService;

    private Pageable pageable;

    @BeforeEach
    void setUp() {
        pageable = PageRequest.of(0, 10);
    }

    @Test
    @DisplayName("1. 회원 출결 내역 조회 성공")
    void testGetAttendanceByNo() throws Exception {
        Page<AttendanceDto> mockPage = new PageImpl<>(List.of());

        when(attendanceService.getAttendanceByNoAndDateRange(
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
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("2. 전체 출결 요약 조회 성공")
    void testGetRecentAttendanceSummary() throws Exception {
        Page<AttendanceDto> mockPage = new PageImpl<>(List.of());

        when(attendanceService.getRecentAttendanceSummary(any(Pageable.class)))
                .thenReturn(mockPage);

        mockMvc.perform(get("/api/v1/attendances/summary/recent")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("3. 특정 회원 근무 통계 조회 성공")
    void testGetRecentWorkingHoursByMember() throws Exception {
        Page<AttendanceSummaryDto> mockPage = new PageImpl<>(List.of());

        when(attendanceService.getRecentWorkingHoursByMember(eq(1L), any(Pageable.class)))
                .thenReturn(mockPage);

        mockMvc.perform(get("/api/v1/attendances/summary/recent/1")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
