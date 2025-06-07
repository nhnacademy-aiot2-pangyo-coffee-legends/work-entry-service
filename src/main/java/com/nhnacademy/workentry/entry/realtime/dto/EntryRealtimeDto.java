package com.nhnacademy.workentry.entry.realtime.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Value;

import java.time.LocalDateTime;

/**
 * 실시간 출입 통계 정보를 담는 DTO 클래스입니다.
 */
@Value
public class EntryRealtimeDto {
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm", timezone = "Asia/Seoul")
    LocalDateTime time; // yyyy-MM-dd HH:mm
    int count;
}
