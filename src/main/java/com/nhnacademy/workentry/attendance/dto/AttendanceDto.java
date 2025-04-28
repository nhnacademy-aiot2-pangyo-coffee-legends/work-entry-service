package com.nhnacademy.workentry.attendance.dto;

import com.nhnacademy.workentry.attendance.entity.Attendance;
import lombok.Value;

import java.time.LocalDateTime;

/**
 * 출결 정보 조회 응답 DTO입니다.
 */
@Value
public class AttendanceDto {
     Long id;
     Long no;
     String name; // 멤버 이름 (FeignClient 통해 가져옴)
     LocalDateTime workDate;
     LocalDateTime inTime;
     LocalDateTime outTime;
     String statusDescription;

    /**
     * Attendance 엔티티와 MemberInfoResponse DTO를 기반으로 AttendanceDto를 생성합니다.
     *
     * @param attendance Attendance 엔티티
     * @param member     MemberInfoResponse (FeignClient를 통해 받은 회원 정보)
     * @return AttendanceDto 인스턴스
     */
    public static AttendanceDto from(Attendance attendance, MemberInfoResponse member) {
        return new AttendanceDto(
                attendance.getId(),
                attendance.getMbNo(),
                member.getName(),
                attendance.getWorkDate(),
                attendance.getInTime(),
                attendance.getOutTime(),
                attendance.getStatus().getDescription()
        );
    }
}
