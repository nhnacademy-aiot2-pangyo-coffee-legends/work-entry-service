package com.nhnacademy.workentry.attendance.bot;

import com.nhnacademy.workentry.adapter.member.client.MemberServiceClient;
import com.nhnacademy.workentry.adapter.member.dto.MemberNoResponse;
import com.nhnacademy.workentry.attendance.dto.AttendanceRequest;
import com.nhnacademy.workentry.attendance.service.AttendanceService;
import com.nhnacademy.workentry.common.time.TodayProvider;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class AttendanceSimulatorBotTest {

    @Mock
    private MemberServiceClient memberServiceClient;

    @Mock
    private AttendanceService attendanceService;

    @InjectMocks
    private AttendanceSimulatorBot bot;

    @Test
    @DisplayName("정상적으로 모든 멤버에 대해 출근 기록 생성")
    void createCheckInAttendanceData_정상작동() {
        List<MemberNoResponse> mockMemberIds = List.of(
                new MemberNoResponse(1L),
                new MemberNoResponse(2L),
                new MemberNoResponse(3L)
        );
        when(memberServiceClient.getAllMemberIds()).thenReturn(mockMemberIds);

        bot.createCheckInAttendanceData();

        verify(attendanceService, times(3)).createAttendance(any(AttendanceRequest.class));
    }

    @Test
    @DisplayName("정상적으로 모든 멤버에 대해 퇴근 기록 생성")
    void createCheckOutAttendanceData_정상작동() {
        List<MemberNoResponse> mockMemberIds = List.of(
                new MemberNoResponse(1L)
        );
        when(memberServiceClient.getAllMemberIds()).thenReturn(mockMemberIds);

        bot.createCheckOutAttendanceData();

        verify(attendanceService).checkOut(eq(1L), eq(LocalDate.now()));
    }

    @Test
    @DisplayName("출근 출결 요청이 정상적으로 생성되어야 함")
    void createCheckInAttendanceData_실제값검증() {
        when(memberServiceClient.getAllMemberIds()).thenReturn(List.of(new MemberNoResponse(99L)));

        bot.createCheckInAttendanceData();

        verify(attendanceService).createAttendance(argThat(req ->
                req.getMbNo().equals(99L) &&
                        req.getCheckIn() != null &&
                        req.getStatus() != null
        ));

    }

    @Test
    @DisplayName("주말 출근 데이터 생성이 정상적으로 생성되어야 함")
    void createWeekendCheckInAttendanceData_정상작동() {
        List<MemberNoResponse> mockMemberIds = List.of(
                new MemberNoResponse(1L),
                new MemberNoResponse(2L),
                new MemberNoResponse(3L)
        );
        when(memberServiceClient.getAllMemberIds()).thenReturn(mockMemberIds);

        TodayProvider sundayProvider = () -> LocalDate.of(2025, 6, 1);

        when(memberServiceClient.getAllMemberIds()).thenReturn(mockMemberIds);

        AttendanceSimulatorBot simulatorBot = new AttendanceSimulatorBot(
                memberServiceClient,
                attendanceService,
                sundayProvider
        );

        simulatorBot.createCheckInAttendanceData();

        verify(attendanceService, atMost(mockMemberIds.size()))
                .createAttendance(any(AttendanceRequest.class));

    }

    @Test
    @DisplayName("출근 레코드가 없으면 넘어가야 함.(주말)")
    void passWeekendCheckInAttendanceData_정상작동() {
        List<MemberNoResponse> mockMemberIds = List.of(
                new MemberNoResponse(1L),
                new MemberNoResponse(2L),
                new MemberNoResponse(3L)
        );
        when(memberServiceClient.getAllMemberIds()).thenReturn(mockMemberIds);

        TodayProvider sundayProvider = () -> LocalDate.of(2025, 6, 1);

        AttendanceSimulatorBot simulatorBot = new AttendanceSimulatorBot(
                memberServiceClient,
                attendanceService,
                sundayProvider
        ) {
            @Override
            protected String decideWeekendAttendanceStatus(){
                return null; // 비출근으로 간주
            }
        };

        when(memberServiceClient.getAllMemberIds()).thenReturn(mockMemberIds);

        simulatorBot.createCheckInAttendanceData();

        verify(attendanceService, never()).createAttendance(any());

    }
}