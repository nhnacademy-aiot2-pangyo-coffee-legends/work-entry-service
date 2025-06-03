package com.nhnacademy.workentry.common.time.impl;

import com.nhnacademy.workentry.common.time.TodayProvider;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;

@Component
public class SystemTodayProvider implements TodayProvider {
    private static final String ASIA_SEOUL = "Asia/Seoul";

    @Override
    public LocalDate getToday() {
        return LocalDate.now(ZoneId.of(ASIA_SEOUL));
    }

}
