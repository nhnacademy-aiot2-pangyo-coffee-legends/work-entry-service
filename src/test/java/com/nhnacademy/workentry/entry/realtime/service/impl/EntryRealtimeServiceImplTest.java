package com.nhnacademy.workentry.entry.realtime.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.QueryApi;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import com.nhnacademy.workentry.entry.realtime.dto.EntryRealtimeDto;
import com.nhnacademy.workentry.log.realtime.LogWebSocketHandler;
import com.nhnacademy.workentry.notify.adapter.NotifyAdapter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.*;

/**
 * {@link EntryRealtimeServiceImpl} 클래스의 단위 테스트 클래스입니다.
 * InfluxDB에서 가져온 출입 데이터의 처리 및 WebSocket 로그 전송 기능을 검증합니다.
 */
@ExtendWith(MockitoExtension.class)
class EntryRealtimeServiceImplTest {

    @Mock
    InfluxDBClient influxDBClient;

    @Mock
    LogWebSocketHandler logWebSocketHandler;

    @Mock
    NotifyAdapter notifyAdapter;

    @InjectMocks
    EntryRealtimeServiceImpl service;

    @Spy
    ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 정상적인 InfluxDB 응답이 있을 때 logWebSocketHandler가 "INFO" 로그를 broadcast 하는지 검증합니다.
     */
    @Test
    @DisplayName("정상 출입 로그 출력 테스트(INFO)")
    void testGetLatestEntry_withValidData_broadcastsInfo() {
        QueryApi mockQueryApi = mock(QueryApi.class);
        FluxRecord mockRecord = mock(FluxRecord.class);
        FluxTable mockTable = mock(FluxTable.class);

        when(influxDBClient.getQueryApi()).thenReturn(mockQueryApi);
        when(mockQueryApi.query(anyString())).thenReturn(List.of(mockTable));
        when(mockTable.getRecords()).thenReturn(List.of(mockRecord));

        OffsetDateTime fakeTime = OffsetDateTime.now().withHour(14);
        when(mockRecord.getTime()).thenReturn(fakeTime.toInstant());
        when(mockRecord.getValue()).thenReturn(3);

        EntryRealtimeDto result = service.getLatestEntry();

        verify(logWebSocketHandler).broadcast(contains("INFO"));
        assertEquals(3, result.getCount());
    }

    /**
     * 심야 시간일 경우 로그 레벨이 "ALERT"로 처리되는지 검증합니다.
     */
    @Test
    @DisplayName("절대 통제 시간대일 경우 이상 출입 로그레벨 출력 테스트(ALERT)")
    void getLatestEntry() {
        LocalDateTime midnight = LocalDateTime.of(2025, 4, 29, 0, 1);

        QueryApi mockQueryApi = mock(QueryApi.class);
        FluxRecord mockRecord = mock(FluxRecord.class);
        FluxTable mockTable = mock(FluxTable.class);

        when(influxDBClient.getQueryApi()).thenReturn(mockQueryApi);
        when(mockQueryApi.query(anyString())).thenReturn(List.of(mockTable));
        when(mockTable.getRecords()).thenReturn(List.of(mockRecord));

        when(mockRecord.getTime()).thenReturn(midnight.atZone(ZoneId.of("Asia/Seoul")).toInstant());
        when(mockRecord.getValue()).thenReturn(1);
        service.getLatestEntry();

        verify(logWebSocketHandler).broadcast(contains("ALERT"));
    }

    /**
     * 오후 시간일 경우 로그 레벨이 "INFO"로 처리되는지 검증합니다.
     */
    @Test
    @DisplayName("오후 시간대일 경우 로그 레벨 출력 테스트(INFO)")
    void testLogAndBroadcast_atAfternoon_logsAsInfo() {
        LocalDateTime afternoon = LocalDateTime.of(2025, 4, 29, 14, 0);

        QueryApi mockQueryApi = mock(QueryApi.class);
        FluxRecord mockRecord = mock(FluxRecord.class);
        FluxTable mockTable = mock(FluxTable.class);

        when(influxDBClient.getQueryApi()).thenReturn(mockQueryApi);
        when(mockQueryApi.query(anyString())).thenReturn(List.of(mockTable));
        when(mockTable.getRecords()).thenReturn(List.of(mockRecord));

        when(mockRecord.getTime()).thenReturn(afternoon.atZone(ZoneId.of("Asia/Seoul")).toInstant());
        when(mockRecord.getValue()).thenReturn(1);
        service.getLatestEntry();

        verify(logWebSocketHandler).broadcast(contains("INFO"));
    }

    /**
     * JSON 직렬화 실패 상황을 강제로 발생시켜 로그 메시지가 "직렬화 실패"로 출력되는지 검증합니다.
     *
     * @throws JsonProcessingException 직렬화 실패를 강제로 유발하기 위해 선언된 예외
     */
    @Test
    @DisplayName("직렬화 실패 로그 메시지 테스트")
    void testLogAndBroadcast_jsonSerializationFails_logsError() throws JsonProcessingException {
        LocalDateTime entryTime = LocalDateTime.of(2025, 4, 29, 1, 0);

        FluxRecord record = mock(FluxRecord.class);
        FluxTable table = mock(FluxTable.class);
        QueryApi queryApi = mock(QueryApi.class);

        when(influxDBClient.getQueryApi()).thenReturn(queryApi);
        when(queryApi.query(anyString())).thenReturn(List.of(table));
        when(table.getRecords()).thenReturn(List.of(record));

        when(record.getTime()).thenReturn(entryTime.atZone(ZoneId.of("Asia/Seoul")).toInstant());
        when(record.getValue()).thenReturn(1);

        doThrow(JsonProcessingException.class)
                .when(objectMapper).writeValueAsString(any(EntryRealtimeDto.class));

        service.getLatestEntry();

        verify(logWebSocketHandler).broadcast(contains("직렬화 실패"));
    }
}