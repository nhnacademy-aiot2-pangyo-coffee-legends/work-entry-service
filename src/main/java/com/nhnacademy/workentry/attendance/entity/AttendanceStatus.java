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

}
