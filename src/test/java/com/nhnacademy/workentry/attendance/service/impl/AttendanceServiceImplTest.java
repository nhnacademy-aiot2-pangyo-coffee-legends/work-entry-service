package com.nhnacademy.workentry.attendance.service.impl;

import com.nhnacademy.workentry.attendance.constant.AttendanceStatusConstants;
import com.nhnacademy.workentry.attendance.dto.AttendanceDto;
import com.nhnacademy.workentry.attendance.dto.AttendanceRequest;
import com.nhnacademy.workentry.attendance.entity.Attendance;
import com.nhnacademy.workentry.attendance.entity.AttendanceStatus;
import com.nhnacademy.workentry.attendance.repository.AttendanceRepository;
import com.nhnacademy.workentry.attendance.repository.AttendanceStatusRepository;
import com.nhnacademy.workentry.common.exception.AttendanceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttendanceServiceImplTest {

    @Mock
    private AttendanceRepository attendanceRepository;

    @Mock
    private AttendanceStatusRepository attendanceStatusRepository;

    @InjectMocks
    private AttendanceServiceImpl attendanceService;

    @Test
    void testGetAttendanceByNo() {
        Long mbNo = 1L;
        Attendance attendance = mock(Attendance.class);
        when(attendanceRepository.findAllByMbNo(mbNo)).thenReturn(List.of(attendance));

        List<AttendanceDto> result = attendanceService.getAttendanceByNo(mbNo);

        assertThat(result).hasSize(1);
        verify(attendanceRepository).findAllByMbNo(mbNo);
    }

    @Test
    void testGetAttendanceByNoAndDateRange() {
        Long mbNo = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        LocalDateTime start = LocalDateTime.now().minusDays(5);
        LocalDateTime end = LocalDateTime.now();

        AttendanceDto mockDto = new AttendanceDto(
                1L, mbNo,
                LocalDate.now(),
                start, end,
                "출근"
        );

        Page<AttendanceDto> expectedPage = new PageImpl<>(List.of(mockDto));

        when(attendanceRepository.getAttendanceByNoAndDateRange(mbNo, start, end, pageable))
                .thenReturn(expectedPage);

        Page<AttendanceDto> result = attendanceService.getAttendanceByNoAndDateRange(mbNo, start, end, pageable);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getNo()).isEqualTo(mbNo);
        verify(attendanceRepository).getAttendanceByNoAndDateRange(mbNo, start, end, pageable);
    }

    @Test
    void testCreateAttendance() {
        AttendanceRequest request = new AttendanceRequest();
        request.setMbNo(1L);
        request.setWorkDate(LocalDate.now());
        request.setCheckIn(LocalDateTime.now());
        request.setCheckOut(LocalDateTime.now().plusHours(8));
        request.setWorkMinutes(480);
        request.setStatus(AttendanceStatusConstants.STATUS_PRESENT);

        AttendanceStatus mockStatus = new AttendanceStatus();
        when(attendanceStatusRepository.findByDescription(request.getStatus()))
                .thenReturn(Optional.of(mockStatus));

        attendanceService.createAttendance(request);

        verify(attendanceRepository, times(1)).save(any(Attendance.class));
    }

    @Test
    void testCheckOut_successful() {
        Long mbNo = 1L;
        LocalDate workDate = LocalDate.now();

        Attendance attendance = mock(Attendance.class);
        AttendanceStatus status = new AttendanceStatus();
        when(attendanceRepository.findByMbNoAndWorkDate(mbNo, workDate)).thenReturn(Optional.of(attendance));
        when(attendance.getStatus()).thenReturn(new AttendanceStatus(null, AttendanceStatusConstants.STATUS_PRESENT));
        when(attendanceStatusRepository.findByDescription(AttendanceStatusConstants.STATUS_PRESENT))
                .thenReturn(Optional.of(status));
        when(attendance.getInTime()).thenReturn(LocalDateTime.now().minusHours(9));

        attendanceService.checkOut(mbNo, workDate);

        verify(attendanceRepository).save(attendance);
    }

    @Test
    void testCheckOut_attendanceNotFound() {
        Long mbNo = 999L;
        LocalDate workDate = LocalDate.now();

        when(attendanceRepository.findByMbNoAndWorkDate(mbNo, workDate)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> attendanceService.checkOut(mbNo, workDate))
                .isInstanceOf(AttendanceNotFoundException.class);
    }

    @Test
    void testGetRecentWorkingHoursByMember_noRecords() {
        Long mbNo = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        Page<Attendance> emptyPage = Page.empty();

        when(attendanceRepository.findByMbNoAndWorkDateBetween(any(), any(), any(), eq(pageable)))
                .thenReturn(emptyPage);

        assertThatThrownBy(() -> attendanceService.getRecentWorkingHoursByMember(mbNo, pageable))
                .isInstanceOf(org.springframework.web.server.ResponseStatusException.class);
    }

    @Test
    void testGetRecentAttendanceSummary() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Attendance> page = new PageImpl<>(List.of(mock(Attendance.class)));

        when(attendanceRepository.findByWorkDateBetween(any(), any(), eq(pageable))).thenReturn(page);

        Page<AttendanceDto> result = attendanceService.getRecentAttendanceSummary(pageable);

        assertThat(result.getTotalElements()).isEqualTo(1);
    }
}