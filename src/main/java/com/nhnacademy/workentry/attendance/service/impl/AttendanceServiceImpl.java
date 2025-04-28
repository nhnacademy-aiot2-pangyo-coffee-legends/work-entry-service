package com.nhnacademy.workentry.attendance.service.impl;

import com.nhnacademy.workentry.attendance.adapter.GatewayAdaptor;
import com.nhnacademy.workentry.attendance.dto.AttendanceDto;
import com.nhnacademy.workentry.attendance.dto.MemberInfoResponse;
import com.nhnacademy.workentry.attendance.entity.Attendance;
import com.nhnacademy.workentry.attendance.repository.AttendanceRepository;
import com.nhnacademy.workentry.attendance.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 출결 정보 처리 서비스 구현 클래스입니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final GatewayAdaptor gatewayAdaptor;

    /**
     * 특정 회원의 전체 출결 기록 조회
     *
     * @param mbNo 회원 번호
     * @return 출결 응답 DTO 리스트
     */
    @Override
    public List<AttendanceDto> getAttendanceByNo(Long mbNo) {
        log.info("전체 출결 조회 요청: no={}", mbNo);

        List<Attendance> attendanceList = attendanceRepository.findAllByMbNo(mbNo);
        List<MemberInfoResponse> memberList = gatewayAdaptor.getMemberInfoList().getBody();

        if (memberList == null) {
            log.error("멤버 리스트 조회 실패");
            return List.of();
        }

        // mbNo를 키로 하는 Map 생성
        Map<Long, MemberInfoResponse> memberMap = memberList.stream()
                .collect(Collectors.toMap(MemberInfoResponse::getNo, member -> member));

        log.debug("조회된 출결 수: {}", attendanceList.size());

        return attendanceList.stream()
                .map(att -> {
                    MemberInfoResponse member = memberMap.get(att.getMbNo());
                    if (member == null) {
                        log.warn("회원 정보 없음: mbNo={}", att.getMbNo());
                        return null;
                    }
                    return AttendanceDto.from(att, member);
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * 특정 회원의 기간별 출결 기록 조회
     *
     * @param no 회원 번호
     * @param start 시작 날짜/시간
     * @param end 종료 날짜/시간
     * @return 출결 응답 DTO 리스트
     */
    @Override
    public List<AttendanceDto> getAttendanceByNoAndDateRange(Long no, LocalDateTime start, LocalDateTime end) {
        log.info("기간별 출결 조회 요청: no={}, from={} to={}", no, start, end);

        List<Attendance> records = attendanceRepository.findByMbNoAndWorkDateBetween(no, start, end);
        log.debug("조회된 출결 수: {}", records.size());

        MemberInfoResponse member = gatewayAdaptor.getMemberByNo(no).getBody();
        if (member == null) {
            log.warn("회원 정보 조회 실패: no={}", no);
            return List.of();
        }

        return records.stream()
                .map(att -> AttendanceDto.from(att, member))
                .collect(Collectors.toList());
    }

    /**
     * 최근 7일간 전체 회원의 출결 기록 요약 조회
     *
     * @return 출결 응답 DTO 리스트
     */
    @Override
    public List<AttendanceDto> getRecentAttendanceSummary() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime weekAgo = now.minusDays(7);

        log.info("최근 7일간 출결 요약 조회: 기간 {} ~ {}", weekAgo.toLocalDate(), now.toLocalDate());

        List<Attendance> records = attendanceRepository.findByWorkDateBetween(weekAgo, now);
        log.debug("총 출결 기록 수: {}", records.size());

        List<MemberInfoResponse> memberList = gatewayAdaptor.getMemberInfoList().getBody();
        if (memberList == null) {
            log.error("멤버 리스트 조회 실패");
            return List.of();
        }

        Map<Long, MemberInfoResponse> memberMap = memberList.stream()
                .collect(Collectors.toMap(MemberInfoResponse::getNo, member -> member));

        return records.stream()
                .map(att -> {
                    MemberInfoResponse member = memberMap.get(att.getMbNo());
                    if (member == null) {
                        log.warn("회원 정보 없음: mbNo={}", att.getMbNo());
                        return null;
                    }
                    return AttendanceDto.from(att, member);
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
