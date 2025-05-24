package com.nhnacademy.workentry.entry.dto;

import lombok.Data;

/**
 * 날짜별 출입 횟수를 나타내는 DTO 클래스입니다.
 */
@Data
public class EntryCountDto {
    private final String date; // yyyy-MM-dd
    private final int count;

    public EntryCountDto(String date, int count) {
        this.date = date;
        this.count = count;
    }


}

