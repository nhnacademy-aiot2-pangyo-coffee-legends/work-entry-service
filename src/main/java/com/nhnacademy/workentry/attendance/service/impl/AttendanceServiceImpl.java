package com.nhnacademy.workentry.attendance.service.impl;

import com.nhnacademy.workentry.attendance.constant.AttendanceStatusConstants;
import com.nhnacademy.workentry.attendance.dto.AttendanceDto;
import com.nhnacademy.workentry.attendance.dto.AttendanceRequest;
import com.nhnacademy.workentry.attendance.dto.AttendanceSummaryDto;
import com.nhnacademy.workentry.attendance.entity.Attendance;
import com.nhnacademy.workentry.attendance.entity.AttendanceStatus;
import com.nhnacademy.workentry.attendance.repository.AttendanceRepository;
import com.nhnacademy.workentry.attendance.repository.AttendanceStatusRepository;
import com.nhnacademy.workentry.attendance.service.AttendanceService;
import com.nhnacademy.workentry.common.exception.AttendanceNotFoundException;
import com.nhnacademy.workentry.common.exception.AttendanceStatusNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.*;
import java.util.List;
import java.util.Random;

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
    private final AttendanceStatusRepository attendanceStatusRepository;
    private static final Random random = new Random();

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
        log.debug("조회된 전체 출결 수: {}", attendanceList.size());

        return attendanceList.stream()
                .map(AttendanceDto::from)
                .toList();
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
    //TODO : dev에 병합 후 LocalDateTime start, end -> LocalDate로 타입 수정
    @Override
    public Page<AttendanceDto> getAttendanceByNoAndDateRange(Long no, LocalDate start, LocalDate end, Pageable pageable) {
        log.info("기간별 출결 조회 요청: no={}, from={} to={}", no, start, end);

        return attendanceRepository.getAttendanceByNoAndDateRange(no, start, end, pageable);
    }

    /**
     * 최근 7일간 전체 출결 요약 정보를 조회합니다.
     *
     * @return 최근 일주일간의 출결 정보 DTO 리스트
     */
    @Override
    public Page<AttendanceDto> getRecentAttendanceSummary(Pageable pageable) {
        LocalDate now = LocalDate.now();
        LocalDate monthAgo = now.minusDays(364);
        Page<Attendance> records = attendanceRepository.findByWorkDateBetween(monthAgo, now, pageable);
        return records.map(AttendanceDto::from);
    }

    /**
     * 특정 회원의 최근 30일간 근무 통계(날짜별 근무시간 등)를 조회합니다.
     *
     * @param no 회원 고유 번호
     * @return 날짜별 근무 통계 DTO 리스트
     * @throws ResponseStatusException 해당 기간 내 출결 기록이 없을 경우 404 응답 예외 발생
     */
    @Override
    public Page<AttendanceSummaryDto> getRecentWorkingHoursByMember(Long no, Pageable pageable) {
        LocalDate now = LocalDate.now();
        LocalDate monthAgo = now.minusDays(364);

        Page<Attendance> records = attendanceRepository.findByMbNoAndWorkDateBetween(no, monthAgo, now.plusDays(1), pageable);

        if (records.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "최근 30일간 근무 기록이 존재하지 않습니다.");
        }

        return records.map(att -> new AttendanceSummaryDto(
                att.getWorkDate().getYear(),
                att.getWorkDate().getMonthValue(),
                att.getWorkDate().getDayOfMonth(),
                att.getWorkMinutes() != null ? att.getWorkMinutes() / 60 : 0,
                att.getInTime(),
                att.getOutTime(),
                att.getStatus() != null ? att.getStatus().getCode() : 0L
        ));
    }

    /**
     * 출근 기록 생성
     */
    @Transactional
    public void createAttendance(AttendanceRequest request) {
        // 실제 외래 키 값(code)이 DB에 저장
        AttendanceStatus status = attendanceStatusRepository.findByDescription(request.getStatus())
                .orElseThrow(() -> new AttendanceNotFoundException(request.getMbNo()));

        Attendance attendance = Attendance.newAttendance(
                request.getMbNo(),
                request.getWorkDate(),
                request.getCheckIn(),
                request.getCheckOut(),
                request.getWorkMinutes(),
                status
        );

        attendanceRepository.save(attendance);
    }

    /**
     * 퇴근 처리 (체크아웃)
     */
    @Transactional
    public void checkOut(Long no, LocalDate workDate) {
        Attendance attendance = attendanceRepository.findByMbNoAndWorkDate(no, workDate)
                .orElseThrow(() -> new AttendanceNotFoundException(no));

        String statusDescription = attendance.getStatus().getDescription();

        // 퇴근 처리가 필요한 상태 목록
        List<String> validStatuses = List.of(
                AttendanceStatusConstants.STATUS_PRESENT,
                AttendanceStatusConstants.STATUS_LATE,
                AttendanceStatusConstants.STATUS_EARLY_LEAVE,
                AttendanceStatusConstants.STATUS_OUTING
        );

        if (!validStatuses.contains(statusDescription)) {
            log.info("no={} 은 퇴근 처리 대상이 아닌 상태({})입니다. 퇴근 생략", no, statusDescription);
            return;
        }

        LocalDateTime checkOutTime = generateCheckOutTimeForStatus(statusDescription);

        AttendanceStatus status = attendanceStatusRepository.findByDescription(statusDescription)
                .orElseThrow(() -> new AttendanceStatusNotFoundException(statusDescription));

        Integer workMinutes = (int) Duration.between(attendance.getInTime(), checkOutTime).toMinutes();

        attendance.updateCheckOut(checkOutTime, workMinutes, status);

        attendanceRepository.save(attendance);
    }

    /**
     * 출결 상태에 따른 체크아웃 시간 생성
     *
     * @param status 출결 상태
     * @return 해당 상태에 맞는 체크아웃 시간, 없으면 null
     */
    private LocalDateTime generateCheckOutTimeForStatus(String status) {
        ZoneId zoneId = ZoneId.of("Asia/Seoul");
        LocalDate today = LocalDate.now(zoneId);
        LocalTime baseTime;

        switch (status) {
            case AttendanceStatusConstants.STATUS_PRESENT,
                 AttendanceStatusConstants.STATUS_LATE,
                 AttendanceStatusConstants.STATUS_OUTING:
                // 정상 퇴근: 18:00 ~ 18:30
                baseTime = LocalTime.of(18, 0).plusMinutes(random.nextInt(31));
                break;
            case AttendanceStatusConstants.STATUS_EARLY_LEAVE:
                // 반차 조퇴: 14:00 ~ 15:30
                baseTime = LocalTime.of(14, 0).plusMinutes(random.nextInt(91));
                break;
            case AttendanceStatusConstants.STATUS_VACATION,
                 AttendanceStatusConstants.STATUS_SICK,
                 AttendanceStatusConstants.STATUS_ABSENT,
                 AttendanceStatusConstants.STATUS_BEREAVEMENT:
            default:
                return null; // 퇴근 기록 없음
        }

        return LocalDateTime.of(today, baseTime);
    }
}
