package com.nhnacademy.workentry.dto;

import com.nhnacademy.workentry.attendance.dto.AttendanceDto;
import com.nhnacademy.workentry.attendance.entity.Attendance;
import com.nhnacademy.workentry.attendance.entity.AttendanceStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link AttendanceDto} 클래스의 단위 테스트입니다.
 * Attendance 엔티티를 AttendanceDto로 변환하는 from() 정적 메서드의 동작을 검증합니다.
 */
class AttendanceDtoTest {

    /**
     * Attendance 엔티티를 AttendanceDto로 변환하는 from() 메서드가
     * 모든 필드를 정확히 매핑하는지 확인하는 테스트입니다.
     */
    @Test
    @DisplayName("Attendance 엔티티 → AttendanceDto 변환 테스트")
    void testFrom() {
        Long id = 100L;
        Long mbNo = 1L;
        LocalDate workDate = LocalDate.now();
        LocalDateTime inTime = LocalDateTime.of(2024, 4, 30, 9, 0);
        LocalDateTime outTime = LocalDateTime.of(2024, 4, 30, 18, 0);
        AttendanceStatus status = new AttendanceStatus(1L, "출석");

        Attendance attendance = Attendance.builder()
                .id(id)
                .mbNo(mbNo)
                .workDate(workDate)
                .inTime(inTime)
                .outTime(outTime)
                .status(status)
                .build();

        AttendanceDto dto = AttendanceDto.from(attendance);

        assertThat(dto.getId()).isEqualTo(id);
        assertThat(dto.getMbNo()).isEqualTo(mbNo);
        assertThat(dto.getWorkDate()).isEqualTo(workDate);
        assertThat(dto.getInTime()).isEqualTo(inTime);
        assertThat(dto.getOutTime()).isEqualTo(outTime);
        assertThat(dto.getStatusDescription()).isEqualTo("출석");
    }
}
