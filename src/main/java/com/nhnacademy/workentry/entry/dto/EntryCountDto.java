package com.nhnacademy.workentry.entry.dto;

import lombok.Value;

/**
 * 날짜별 출입 횟수를 나타내는 DTO 클래스입니다.
 */
@Value
public class EntryCountDto {
    String date; // yyyy-MM-dd
    int count;

}

