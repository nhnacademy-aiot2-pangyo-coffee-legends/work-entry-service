package com.nhnacademy.workentry.entry.realtime.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 실시간 출입 통계 정보를 담는 DTO 클래스입니다.
 */
@Data
@AllArgsConstructor
public class EntryRealtimeDto {
    private String time; // yyyy-MM-dd HH:mm
    private int count;
}
