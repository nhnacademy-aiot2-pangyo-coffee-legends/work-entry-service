package com.nhnacademy.workentry.attendance.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nhnacademy.workentry.attendance.entity.Attendance;
import com.querydsl.core.annotations.QueryProjection;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 출결 정보 조회 응답 DTO입니다.
 */
@Value
public class AttendanceDto {

    @JsonProperty(value = "attendanceId")
    @NotNull(message = "출결 아이디가 비어있습니다.")
     Long id;

    @JsonProperty(value = "memberNo")
    @NotNull(message = "멤버 아이디가 비어있습니다.")
     Long mbNo;
    @NotNull(message = "출결 날짜가 비어있습니다.")
     LocalDate workDate;

     LocalDateTime inTime;

     LocalDateTime outTime;

     @NotNull(message = "출결 상태 정보가 비어있습니다.")
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
                attendance.getStatus() != null ? attendance.getStatus().getDescription() : null
        );
    }

    /**
     * QueryDSL에서 {@link AttendanceDto}를 projection 대상으로 사용할 수 있도록 하는 생성자입니다.
     * <p>
     * 이 생성자는 {@code QAttendanceDto} 클래스를 통해 QueryDSL의 JPQL 쿼리 결과를 직접 매핑할 때 사용됩니다.
     * DTO의 각 필드는 쿼리에서 직접 선택(select)되어 주입됩니다.
     *
     * @param id 출결 ID
     * @param mbNo 회원 번호
     * @param workDate 근무 일자
     * @param inTime 출근 시간
     * @param outTime 퇴근 시간
     * @param statusDescription 출결 상태 설명
     */
    @QueryProjection
    public AttendanceDto(Long id, Long mbNo, LocalDate workDate, LocalDateTime inTime, LocalDateTime outTime, String statusDescription){
        this.id = id;
        this.mbNo = mbNo;
        this.workDate = workDate;
        this.inTime = inTime;
        this.outTime = outTime;
        this.statusDescription = statusDescription;
    }
}
