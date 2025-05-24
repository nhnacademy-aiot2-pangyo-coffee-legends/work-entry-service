package com.nhnacademy.workentry.attendance.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 출결정보 생성하는 Dto입니다.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceRequest {
    private Long mbNo;
    private LocalDate workDate;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
    private Integer workMinutes;
    private String status;

}
