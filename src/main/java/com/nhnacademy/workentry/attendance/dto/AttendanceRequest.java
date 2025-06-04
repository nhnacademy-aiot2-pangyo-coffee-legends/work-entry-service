package com.nhnacademy.workentry.attendance.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 출결정보 생성하는 Dto입니다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceRequest {

    @NotNull(message = "멤버 아이디가 비어있습니다.")
    private Long mbNo;

    @NotNull(message = "출결 기간이 비어있습니다.")
    private LocalDate workDate;

    private LocalDateTime checkIn;

    private LocalDateTime checkOut;

    private Integer workMinutes;

    @NotNull(message = "출결 정보가 비어있습니다.")
    private String status;

}
