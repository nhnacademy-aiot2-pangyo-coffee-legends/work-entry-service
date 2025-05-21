package com.nhnacademy.workentry.attendance.bot;

import com.nhnacademy.workentry.adaptor.member.client.MemberServiceClient;
import com.nhnacademy.workentry.adaptor.member.dto.MemberNoResponse;
import com.nhnacademy.workentry.attendance.constant.AttendanceStatusConstants;
import com.nhnacademy.workentry.attendance.dto.AttendanceRequest;
import com.nhnacademy.workentry.attendance.service.AttendanceService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
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
        // given
        List<MemberNoResponse> mockMemberIds = List.of(
                MemberNoResponse.of(1L),
                MemberNoResponse.of(2L),
                MemberNoResponse.of(3L)
        );
        when(memberServiceClient.getAllMemberIds()).thenReturn(mockMemberIds);

        // when
        bot.createCheckInAttendanceData();

        // then
        verify(attendanceService, times(3)).createAttendance(any(AttendanceRequest.class));
    }

    @Test
    @DisplayName("정상적으로 모든 멤버에 대해 퇴근 기록 생성")
    void createCheckOutAttendanceData_정상작동() {
        // given
        List<MemberNoResponse> mockMemberIds = List.of(
                MemberNoResponse.of(1L)
        );
        when(memberServiceClient.getAllMemberIds()).thenReturn(mockMemberIds);

        // when
        bot.createCheckOutAttendanceData();

        // then
        verify(attendanceService).checkOut(eq(1L), eq(LocalDate.now()));
    }

    @Test
    @DisplayName("출근 출결 요청이 정상적으로 생성되어야 함")
    void createCheckInAttendanceData_실제값검증() {
        when(memberServiceClient.getAllMemberIds()).thenReturn(List.of(MemberNoResponse.of(99L)));

        bot.createCheckInAttendanceData();

        verify(attendanceService).createAttendance(argThat(req ->
                req.getMbNo().equals(99L) &&
                        req.getCheckIn() != null &&
                        req.getStatus() != null
        ));

    }
}