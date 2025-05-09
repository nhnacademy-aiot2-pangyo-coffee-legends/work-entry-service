package com.nhnacademy.workentry.attendance.service.impl;

import com.nhnacademy.workentry.attendance.dto.AttendanceDto;
import com.nhnacademy.workentry.attendance.dto.AttendanceSummaryDto;
import com.nhnacademy.workentry.attendance.entity.Attendance;
import com.nhnacademy.workentry.attendance.repository.AttendanceRepository;
import com.nhnacademy.workentry.attendance.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 출결 정보 처리 서비스 구현 클래스입니다.
 * <p>
 * 회원의 출결 기록 조회, 요약 통계 생성, 날짜별 조회 기능 등을 제공합니다.
 * 데이터 조회 후 DTO로 변환하여 외부에 전달되도록 구성되어 있습니다.
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;

    /**
     * 특정 회원의 전체 출결 기록을 조회합니다.
     *
     * @param mbNo 회원 고유 번호
     * @return 출결 정보 DTO 리스트
     */
    @Override
    public List<AttendanceDto> getAttendanceByNo(Long mbNo) {
        log.info("전체 출결 조회 요청: no={}", mbNo);

        List<Attendance> attendanceList = attendanceRepository.findAllByMbNo(mbNo);
        log.debug("조회된 출결 수: {}", attendanceList.size());

        return attendanceList.stream()
                .map(AttendanceDto::from)
                .collect(Collectors.toList());
    }
    /**
     * 특정 회원의 지정된 기간 내 출결 기록을 페이지 단위로 조회합니다.
     *
     * @param no 회원 고유 번호
     * @param start 시작 시간
     * @param end 종료 시간
     * @param pageable 페이지 정보
     * @return 페이지 형태의 출결 DTO 목록
     */
    @Override
    public Page<AttendanceDto> getAttendanceByNoAndDateRange(Long no, LocalDateTime start, LocalDateTime end, Pageable pageable) {
        log.info("기간별 출결 조회 요청: no={}, from={} to={}", no, start, end);

        Page<Attendance> records = attendanceRepository.findByMbNoAndWorkDateBetween(no, start, end, pageable);
        log.debug("조회된 출결 수: {}", records.getTotalElements());

        return records.map(AttendanceDto::from);
    }



    @Override
    public Page<AttendanceDto> getRecentAttendanceSummary(Pageable pageable) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime monthAgo = now.minusDays(30);
        Page<Attendance> records = attendanceRepository.findByWorkDateBetween(monthAgo, now, pageable);
        return records.map(AttendanceDto::from);
    }

    @Override
    public List<AttendanceSummaryDto> getRecentWorkingHoursByMember(Long no) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime monthAgo = now.minusDays(30);

        List<Attendance> records = attendanceRepository.findByMbNoAndWorkDateBetween(no, monthAgo, now.plusDays(1));

        if (records.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "최근 30일간 근무 기록이 존재하지 않습니다.");
        }

        return records.stream()
                .map(att -> new AttendanceSummaryDto(
                        att.getWorkDate().getYear(),
                        att.getWorkDate().getMonthValue(),
                        att.getWorkDate().getDayOfMonth(),
                        att.getWorkMinutes() != null ? att.getWorkMinutes() / 60 : 0,
                        att.getInTime(),
                        att.getOutTime(),
                        att.getStatus() != null ? att.getStatus().getCode() : 0L
                ))
                .collect(Collectors.toList());
    }

}
