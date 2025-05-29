package com.nhnacademy.workentry.entry.controller;


import com.nhnacademy.workentry.entry.dto.EntryCountDto;
import com.nhnacademy.workentry.entry.realtime.dto.EntryRealtimeDto;
import com.nhnacademy.workentry.entry.realtime.service.EntryRealtimeService;
import com.nhnacademy.workentry.entry.realtime.service.impl.EntryRealtimeServiceImpl;
import com.nhnacademy.workentry.entry.service.EntryService;
import com.nhnacademy.workentry.entry.service.impl.EntryServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

/**
 * 출입 통계 관련 API를 제공하는 컨트롤러 클래스입니다.
 */

@Slf4j
@RestController
@RequestMapping("/api/v1/entries")
@RequiredArgsConstructor
public class EntryController {
    private final EntryService entryService;
    private final EntryRealtimeService entryRealtimeService;

    /**
     * 주간 출입 통계를 조회합니다.
     * @return 날짜별 출입 횟수를 담은 EntryCountDto 리스트
     */
    @GetMapping("/weekly")
    public List<EntryCountDto> getMonthlyEntryCounts() {
        try {
            List<EntryCountDto> result = entryService.getMonthlyEntryCounts();
            log.info("[주간 출입 통계] 조회됨: {}", result);
            return result;
        } catch (Exception e) {
            log.error("[주간 출입 통계] 조회 실패", e);
            return Collections.emptyList(); // 실패 시 빈 리스트 반환
        }
    }

    /**
     * 최근 하루 출입 통계(실시간)를 조회합니다.
     *
     * @return EntryCountDto 객체
     */
    @GetMapping("/realtime")
    public EntryRealtimeDto getRealtimeEntry() {
        try {
            EntryRealtimeDto dto = entryRealtimeService.getLatestEntry();
            log.info("[실시간 출입 통계] 조회됨: {}", dto);
            return dto;
        } catch (Exception e) {
            log.error("[실시간 출입 통계] 조회 실패", e);
            return new EntryRealtimeDto("N/A", 0); // 실패 시 기본값
        }
    }
}
