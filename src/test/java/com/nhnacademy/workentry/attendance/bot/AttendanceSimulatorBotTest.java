package com.nhnacademy.workentry.attendance.bot;

import com.nhnacademy.workentry.adaptor.member.client.MemberServiceClient;
import com.nhnacademy.workentry.adaptor.member.dto.MemberNoResponse;
import com.nhnacademy.workentry.attendance.constant.AttendanceStatusConstants;
import com.nhnacademy.workentry.attendance.dto.AttendanceRequest;
import com.nhnacademy.workentry.attendance.service.AttendanceService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

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
        verify(attendanceService).checkOut(eq(1L), eq(LocalDate.now()), any(LocalDateTime.class), eq(AttendanceStatusConstants.STATUS_PRESENT));
    }

    @Test
    @DisplayName("출근 요청에 정확한 값이 포함되어야 함")
    void createCheckInAttendanceData_실제값검증() {
        // given
        when(memberServiceClient.getAllMemberIds()).thenReturn(List.of(MemberNoResponse.of(99L)));

        ArgumentCaptor<AttendanceRequest> captor = ArgumentCaptor.forClass(AttendanceRequest.class);

        // when
        bot.createCheckInAttendanceData();

        // then
        verify(attendanceService).createAttendance(captor.capture());

        AttendanceRequest request = captor.getValue();
        assertThat(request.getMbNo()).isEqualTo(99L);
        assertThat(request.getStatus()).isEqualTo(AttendanceStatusConstants.STATUS_PRESENT);
        assertThat(request.getCheckIn()).isNotNull();
    }
}