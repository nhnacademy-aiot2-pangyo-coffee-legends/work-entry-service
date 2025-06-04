package com.nhnacademy.workentry.common.time;

import java.time.LocalDate;

/**
 * 현재 날짜를 제공하는 인터페이스입니다.
 * 이 인터페이스를 구현하면, 시스템의 현재 날짜나 테스트용 고정 날짜 등 다양한 방식으로 날짜를 주입할 수 있습니다.
 */
public interface TodayProvider {
    LocalDate getToday();
}
