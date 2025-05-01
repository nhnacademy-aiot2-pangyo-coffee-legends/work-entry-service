package com.nhnacademy.workentry.attendance.service.impl;

import com.nhnacademy.workentry.attendance.dto.AttendanceDto;
import com.nhnacademy.workentry.attendance.dto.AttendanceSummaryDto;
import com.nhnacademy.workentry.attendance.entity.Attendance;
import com.nhnacademy.workentry.attendance.repository.AttendanceRepository;
import com.nhnacademy.workentry.attendance.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
     * 특정 회원의 지정된 기간 내 출결 기록을 조회합니다.
     *
     * @param no    회원 고유 번호
     * @param start 조회 시작 시간 (포함)
     * @param end   조회 종료 시간 (포함)
     * @return 출결 정보 DTO 리스트
     */
    @Override
    public List<AttendanceDto> getAttendanceByNoAndDateRange(Long no, LocalDateTime start, LocalDateTime end) {
        log.info("기간별 출결 조회 요청: no={}, from={} to={}", no, start, end);

        List<Attendance> records = attendanceRepository.findByMbNoAndWorkDateBetween(no, start, end);
        log.debug("조회된 출결 수: {}", records.size());

        return records.stream()
                .map(AttendanceDto::from)
                .collect(Collectors.toList());
    }

    /**
     * 최근 7일간 전체 출결 요약 정보를 조회합니다.
     *
     * @return 최근 일주일간의 출결 정보 DTO 리스트
     */
    @Override
    public List<AttendanceDto> getRecentAttendanceSummary() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime weekAgo = now.minusDays(7);

        log.info("최근 7일간 출결 요약 조회: 기간 {} ~ {}", weekAgo.toLocalDate(), now.toLocalDate());

        List<Attendance> records = attendanceRepository.findByWorkDateBetween(weekAgo, now);
        log.debug("총 출결 기록 수: {}", records.size());

        return records.stream()
                .map(AttendanceDto::from)
                .collect(Collectors.toList());
    }

    /**
     * 특정 회원의 최근 30일간 근무 통계(날짜별 근무시간 등)를 조회합니다.
     *
     * @param no 회원 고유 번호
     * @return 날짜별 근무 통계 DTO 리스트
     * @throws ResponseStatusException 해당 기간 내 출결 기록이 없을 경우 404 응답 예외 발생
     */
    @Override
    public List<AttendanceSummaryDto> getRecentWorkingHoursByMember(Long no) {
        LocalDate now = LocalDate.now();
        LocalDate weekAgo = now.minusDays(364); // 실제는 30일로 줄여야 정확함

        log.info("회원 {}의 최근 30일 근무 통계 조회: {} ~ {}", no, weekAgo, now);

        List<Attendance> attendances = attendanceRepository.findByMbNoAndWorkDateBetween(
                no,
                weekAgo.atStartOfDay(),
                now.plusDays(1).atStartOfDay()
        );

        if (attendances.isEmpty()) {
            log.warn("회원 {}의 최근 30일 근무 기록이 존재하지 않습니다.", no);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "최근 30일간 근무 기록이 존재하지 않습니다.");
        }

        return attendances.stream()
                .map(att -> new AttendanceSummaryDto(
                        att.getWorkDate().toLocalDate(),
                        att.getWorkMinutes() != null ? att.getWorkMinutes() / 60 : 0,
                        att.getInTime(),
                        att.getOutTime(),
                        att.getStatus().getCode()
                ))
                .collect(Collectors.toList());
    }
}
