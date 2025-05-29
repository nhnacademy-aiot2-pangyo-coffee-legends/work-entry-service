package com.nhnacademy.workentry.attendance.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 출결 상태 정보를 담는 엔티티입니다.
 * <p>
 * 출석, 지각, 결석 등 각 상태에 대한 코드와 설명을 관리합니다.
 * </p>
 */
@Getter
@Entity
@Table(name = "attendance_status")
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceStatus {

    /**
     * 상태 코드 (예: 1 = 출석)
     */
    @Id
    @Column(name = "code")
    private Long code;

    /**
     * 상태 설명 (예: 출석, 지각 등)
     */
    @Column(name = "description", nullable = false)
    private String description;

    /**
     * 상태 설명을 반환합니다.
     *
     * @return 상태 설명 문자열
     */
    public String getDescription() {
        return description;
    }

    /**
     * 상태 코드를 반환합니다.
     *
     * @return 상태 코드
     */
    public Long getCode() {
        return code;
    }

    /**
     * 주어진 코드 값을 기반으로 AttendanceStatus 인스턴스를 생성합니다.
     * (주의: description은 포함되지 않으며, 단순히 코드 값만 설정됩니다.)
     *
     * @param code 상태 코드 값
     * @return 생성된 AttendanceStatus 객체
     */
    public static AttendanceStatus fromCode(int code) {
        AttendanceStatus status = new AttendanceStatus();
        status.code = (long) code;
        return status;
    }
}
