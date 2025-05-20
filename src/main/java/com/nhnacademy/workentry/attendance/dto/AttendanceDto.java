package com.nhnacademy.workentry.attendance.dto;

import com.nhnacademy.workentry.attendance.entity.Attendance;
import lombok.Value;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 출결 정보 조회 응답 DTO입니다.
 */
@Value
public class AttendanceDto {
     Long id;
     Long no;
     LocalDate workDate;
     LocalDateTime inTime;
     LocalDateTime outTime;
     String statusDescription;

    /**
     * Attendance 엔티티와 MemberInfoResponse DTO를 기반으로 AttendanceDto를 생성합니다.
     *
     * @param attendance Attendance 엔티티
     * @return AttendanceDto 인스턴스
     */
    public static AttendanceDto from(Attendance attendance) {
        return new AttendanceDto(
                attendance.getId(),
                attendance.getMbNo(),
                attendance.getWorkDate(),
                attendance.getInTime(),
                attendance.getOutTime(),
                attendance.getStatus().getDescription()
        );
    }
}
