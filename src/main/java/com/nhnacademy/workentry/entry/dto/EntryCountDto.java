package com.nhnacademy.workentry.entry.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Value;

/**
 * 날짜별 출입 횟수를 나타내는 DTO 클래스입니다.
 */
@Value
public class EntryCountDto {
    @NotNull(message = "출입 날짜가 비어있습니다.")
    String date; // yyyy-MM-dd
    int count;

}

