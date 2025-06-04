package com.nhnacademy.workentry.service;

import com.nhnacademy.workentry.attendance.dto.AttendanceDto;
import com.nhnacademy.workentry.attendance.dto.AttendanceSummaryDto;
import com.nhnacademy.workentry.attendance.entity.Attendance;
import com.nhnacademy.workentry.attendance.entity.AttendanceStatus;
import com.nhnacademy.workentry.attendance.repository.AttendanceRepository;
import com.nhnacademy.workentry.attendance.service.impl.AttendanceServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * {@link AttendanceServiceImpl} 클래스에 대한 단위 테스트입니다.
 * 출결 정보 조회 및 통계 기능의 정상 동작 여부를 검증합니다.
 */
@ExtendWith(MockitoExtension.class)
class AttendanceServiceImplTest {

    @Mock
    private AttendanceRepository attendanceRepository;

    @InjectMocks
    private AttendanceServiceImpl attendanceService;

    private Attendance sampleAttendance;

    /**
     * 테스트 실행 전 공통 데이터 초기화 메서드입니다.
     */
    @BeforeEach
    void setUp() {
        sampleAttendance = Attendance.builder()
                .id(1L)
                .mbNo(99L)
                .workDate(LocalDate.now())
                .inTime(LocalDateTime.of(2024, 4, 10, 9, 0))
                .outTime(LocalDateTime.of(2024, 4, 10, 18, 0))
                .status(new AttendanceStatus(1L,"출석"))
                .workMinutes(540)
                .build();
    }

    /**
     * 회원 번호로 전체 출결 목록을 정상 조회할 수 있는 경우를 검증합니다.
     */
    @Test
    @DisplayName("1. 회원 전체 출결 조회 성공")
    void testGetAttendanceByNo() {
        when(attendanceRepository.findAllByMbNo(99L)).thenReturn(List.of(sampleAttendance));

        List<AttendanceDto> results = attendanceService.getAttendanceByNo(99L);

        assertThat(results).hasSize(1);
        assertThat(results.getFirst().getMbNo()).isEqualTo(99L);
        verify(attendanceRepository).findAllByMbNo(99L);
    }

    /**
     * 특정 기간 내 회원 출결 기록을 페이지 단위로 조회할 수 있는 경우를 검증합니다.
     */
    @Test
    @DisplayName("2. 회원 기간별 출결 조회 성공")
    void testGetAttendanceByNoAndDateRange() {
        Pageable pageable = PageRequest.of(0, 10);
        AttendanceDto dto = new AttendanceDto(
                1L,
                99L,
                LocalDate.of(2024, 4, 15),
                LocalDateTime.of(2024, 4, 15, 9, 0),
                LocalDateTime.of(2024, 4, 15, 18, 0),
                "출근"
        );
        Page<AttendanceDto> mockPage = new PageImpl<>(List.of(dto), pageable, 1);

        when(attendanceRepository.getAttendanceByNoAndDateRange(any(), any(), any(), any()))
                .thenReturn(mockPage);

        Page<AttendanceDto> result = attendanceService.getAttendanceByNoAndDateRange(
                99L,
                LocalDate.now(),
                LocalDate.now(),
                pageable
        );

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getMbNo()).isEqualTo(99L);
    }

    /**
     * 최근 30일 전체 출결 요약 정보를 정상 조회할 수 있는 경우를 검증합니다.
     */
    @Test
    @DisplayName("3. 최근 30일 전체 출결 요약 조회 성공")
    void testGetRecentAttendanceSummary() {
        Pageable pageable = PageRequest.of(0, 10);
        when(attendanceRepository.findByWorkDateBetween(any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(sampleAttendance), pageable, 1));

        Page<AttendanceDto> result = attendanceService.getRecentAttendanceSummary(pageable);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().getFirst().getStatusDescription()).isEqualTo("출석");
    }
    /**
     * 최근 30일 회원 근무 통계 데이터를 성공적으로 반환하는 경우를 테스트합니다.
     * <p>
     * AttendanceRepository에서 반환된 Attendance 데이터를 기반으로,
     * AttendanceSummaryDto 객체로 정상 변환되는지 검증합니다.
     * hoursWorked, inTime, outTime, statusCode 값까지 정확히 일치하는지 확인합니다.
     * </p>
     */
    @Test
    @DisplayName("4. 최근 30일 회원 근무 통계 조회 성공")
    void testGetRecentWorkingHoursByMember_success() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        when(attendanceRepository.findByMbNoAndWorkDateBetween(any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(sampleAttendance), pageable, 1));

        // when
        Page<AttendanceSummaryDto> result = attendanceService.getRecentWorkingHoursByMember(99L, pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        AttendanceSummaryDto dto = result.getContent().getFirst();
        assertThat(dto.getHoursWorked()).isEqualTo(9); // 'getTotalHours()' → 'getHoursWorked()'로 수정
        assertThat(dto.getInTime()).isEqualTo(sampleAttendance.getInTime());
        assertThat(dto.getOutTime()).isEqualTo(sampleAttendance.getOutTime());
        assertThat(dto.getCode()).isEqualTo(sampleAttendance.getStatus().getCode());
    }

    /**
     * 최근 30일간 출결 데이터가 존재하지 않을 경우 404 예외가 발생하는 경우를 검증합니다.
     */
    @Test
    @DisplayName("5. 최근 30일 근무 통계 없음 - 예외 발생")
    void testGetRecentWorkingHoursByMember_notFound() {
        Pageable pageable = PageRequest.of(0, 10);
        when(attendanceRepository.findByMbNoAndWorkDateBetween(any(), any(), any(), any()))
                .thenReturn(Page.empty());

        assertThrows(ResponseStatusException.class, () ->
                attendanceService.getRecentWorkingHoursByMember(99L, pageable));
    }
}
