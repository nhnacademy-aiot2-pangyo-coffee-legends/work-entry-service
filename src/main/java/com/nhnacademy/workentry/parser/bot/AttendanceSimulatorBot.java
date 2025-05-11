package com.nhnacademy.workentry.parser.bot;

import com.nhnacademy.workentry.adaptor.MemberAdaptor;
import com.nhnacademy.workentry.attendance.dto.AttendanceRequest;
import com.nhnacademy.workentry.attendance.entity.Attendance;
import com.nhnacademy.workentry.attendance.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AttendanceSimulatorBot {
    private final MemberAdaptor memberAdaptor;
    private final AttendanceService attendanceService; // JPA나 Repository를 감싸는 서비스

    // 매일 오전 9시에 체크인 생성
    @Scheduled(cron = "0 0 9 * * *")
    public void createCheckInAttendanceData() {
        List<Long> memberIds = memberAdaptor.getAllMemberIds();
        LocalDate today = LocalDate.now();
        LocalDateTime checkInTime = LocalDateTime.now();

        for (Long mbNo : memberIds) {
            // 새로운 출근 레코드 생성
            attendanceService.createAttendance(
                    new AttendanceRequest(mbNo, today, checkInTime, null, null, "출석")
            );
        }
    }

    // 매일 오후 6시에 체크아웃 생성
    @Scheduled(cron = "0 0 18 * * *")
    public void createCheckOutAttendanceData() {
        List<Long> memberIds = memberAdaptor.getAllMemberIds();
        LocalDate today = LocalDate.now();
        LocalDateTime checkOutTime = LocalDateTime.now();

        for (Long mbNo : memberIds) {
            // 1) 오늘 출근 기록 조회
            Attendance attendance = attendanceService
                    .findByMbNoAndDate(mbNo, today)
                    .orElseThrow(() -> new IllegalStateException("출근 기록 없음: " + mbNo));

            LocalDateTime checkInTime = attendance.getInTime();

            // 2) 근무 시간 계산
            Duration workDuration = Duration.between(checkInTime, checkOutTime);

            // 3) 출근 레코드 업데이트
            attendance.setCheckOutTime(checkOutTime);
            attendance.setWorkDuration(workDuration);
            attendance.setStatus("출석");

            attendanceService.updateAttendance(attendance);
        }
    }
}
