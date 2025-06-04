package com.nhnacademy.workentry.entry.realtime.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Value;

/**
 * 실시간 출입 통계 정보를 담는 DTO 클래스입니다.
 */
@Value
public class EntryRealtimeDto {
    @NotNull(message = "출입 시간이 비어있습니다.")
    String time; // yyyy-MM-dd HH:mm
    int count;
}
