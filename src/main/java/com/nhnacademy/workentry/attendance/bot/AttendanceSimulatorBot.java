package com.nhnacademy.workentry.attendance.bot;

import com.nhnacademy.workentry.adaptor.MemberAdaptor;
import com.nhnacademy.workentry.adaptor.client.MemberServiceClient;
import com.nhnacademy.workentry.attendance.constant.AttendanceStatusConstants;
import com.nhnacademy.workentry.attendance.dto.AttendanceRequest;
import com.nhnacademy.workentry.attendance.service.AttendanceService;
import com.nhnacademy.workentry.common.exception.MemberNotFoundException;
import feign.FeignException;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AttendanceSimulatorBot {
    private final MemberAdaptor memberAdaptor;
    private final MemberServiceClient memberServiceClient;
    private final AttendanceService attendanceService;

    // 매일 오전 9시에 체크인 생성
    @Scheduled(cron = "0 0 9 * * *", zone = "Asia/Seoul")
    public void createCheckInAttendanceData() {
        log.info("스케줄 실행: {}", LocalDateTime.now());
        List<Long> memberIds = new ArrayList<>();
        try{
            memberIds = memberServiceClient.getAllMemberIds();
        }catch(FeignException.NotFound e){
            throw new MemberNotFoundException("FeginClient : 멤버 정보를 찾을 수 없습니다.");
        }

        LocalDate today = LocalDate.now();
        LocalDateTime checkInTime = LocalDateTime.now();

        for (Long mbNo : memberIds) {
            // 새로운 출근 레코드 생성
            attendanceService.createAttendance(
                    new AttendanceRequest(mbNo, today, checkInTime, null, null, AttendanceStatusConstants.STATUS_PRESENT)
            );
        }
    }

    // 매일 오후 6시에 체크아웃 생성
    @Scheduled(cron = "0 0 18 * * *", zone = "Asia/Seoul")
    public void createCheckOutAttendanceData() {
        List<Long> memberIds = memberAdaptor.getAllMemberIds();
        LocalDate today = LocalDate.now();
        LocalDateTime checkOutTime = LocalDateTime.now();

        for (Long mbNo : memberIds) {
            attendanceService.checkOut(mbNo, today, checkOutTime, AttendanceStatusConstants.STATUS_PRESENT);
        }
    }
}