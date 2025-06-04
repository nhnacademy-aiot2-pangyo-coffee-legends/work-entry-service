package com.nhnacademy.workentry.common.time.impl;

import com.nhnacademy.workentry.common.time.TodayProvider;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;

/**
 * 시스템의 현재 날짜를 "Asia/Seoul" 시간대를 기준으로 반환하는 {@link TodayProvider} 구현체입니다.
 *
 * <p>이 구현체는 주로 실제 운영 환경에서 사용되며, 현재 시각을 기준으로 {@link LocalDate}를 제공합니다.</p>
 */
@Component
public class SystemTodayProvider implements TodayProvider {
    private static final String ASIA_SEOUL = "Asia/Seoul";

    @Override
    public LocalDate getToday() {
        return LocalDate.now(ZoneId.of(ASIA_SEOUL));
    }

}
