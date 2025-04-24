package com.nhnacademy.workentry.parser.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record AttendanceRecord(
        Long mbNo,
        LocalDate workDate,
        LocalDateTime checkIn,
        LocalDateTime checkOut,
        long minutesWorked,
        String status
) {}
