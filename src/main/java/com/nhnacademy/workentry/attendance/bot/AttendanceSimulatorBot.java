package com.nhnacademy.workentry.attendance.bot;

import com.nhnacademy.workentry.adapter.member.client.MemberServiceClient;
import com.nhnacademy.workentry.adapter.member.dto.MemberNoResponse;
import com.nhnacademy.workentry.attendance.constant.AttendanceStatusConstants;
import com.nhnacademy.workentry.attendance.dto.AttendanceRequest;
import com.nhnacademy.workentry.attendance.service.AttendanceService;
import com.nhnacademy.workentry.common.exception.AttendanceNotFoundException;
import com.nhnacademy.workentry.common.exception.MemberNotFoundException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@Component
@RequiredArgsConstructor
public class AttendanceSimulatorBot {
    private final MemberServiceClient memberServiceClient;
    private final AttendanceService attendanceService;

    private final Random random = new Random();

    // 매일 오전 9시에 체크인 생성
    @Scheduled(cron = "0 0 9 * * *", zone = "Asia/Seoul")
    public void createCheckInAttendanceData() {
        log.info("오전 스케줄 실행: {}", LocalDateTime.now());
        List<MemberNoResponse> memberIds = new ArrayList<>();
        try{
            memberIds = memberServiceClient.getAllMemberIds();
        }catch(FeignException.NotFound e){
            throw new MemberNotFoundException("FeginClient : 멤버 정보를 찾을 수 없습니다.");
        }

        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));

        for (MemberNoResponse mbNo : memberIds) {
            String status = decideAttendanceStatus();

            // 새로운 출근 레코드 생성
            attendanceService.createAttendance(
                    new AttendanceRequest(mbNo.no(), today, generateCheckInTimeForStatus(status), null, null, status)
            );
        }
    }

    // 매일 오후 6시에 체크아웃 생성
    @Scheduled(cron = "0 0 18 * * *", zone = "Asia/Seoul")
    public void createCheckOutAttendanceData() {
        log.info("오후 스케줄 실행: {}", LocalDateTime.now());
        List<MemberNoResponse> memberIds = memberServiceClient.getAllMemberIds();
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));

        for (MemberNoResponse mbNo : memberIds) {
            try{
                attendanceService.checkOut(mbNo.no(), today);
            }catch (AttendanceNotFoundException e){
                log.warn("체크인 기록 없음: memberNo={}, date={}", mbNo.no(), today);
            }
        }
    }

    /**
     * 랜덤한 출결 상태를 결정하는 메서드입니다.
     *
     * 출석 상태는 다른 상태들보다 더 높은 확률(60%)로 선택됩니다.
     * 나머지 상태들은 각각 5~10% 사이의 확률로 분포되어 있으며,
     * 전체 확률 분포는 아래와 같습니다:
     * <ul>
     *     <li>출근 (60%)</li>
     *     <li>지각 (10%)</li>
     *     <li>결근 (5%)</li>
     *     <li>외근 (5%)</li>
     *     <li>연차 (5%)</li>
     *     <li>질병/입원 (5%)</li>
     *     <li>반차 (5%)</li>
     *     <li>상(喪) (5%)</li>
     * </ul>
     *
     * @return 랜덤으로 선택된 출결 상태 상수 문자열
     */
    private String decideAttendanceStatus() {
        int randValue = (int) (Math.random() * 100); // 0~99

        if (randValue < 60) {
            return AttendanceStatusConstants.STATUS_PRESENT;        // 0 ~ 59
        } else if (randValue < 70) {
            return AttendanceStatusConstants.STATUS_LATE;           // 60 ~ 69
        } else if (randValue < 75) {
            return AttendanceStatusConstants.STATUS_ABSENT;         // 70 ~ 74
        } else if (randValue < 80) {
            return AttendanceStatusConstants.STATUS_OUTING;         // 75 ~ 79
        } else if (randValue < 85) {
            return AttendanceStatusConstants.STATUS_VACATION;       // 80 ~ 84
        } else if (randValue < 90) {
            return AttendanceStatusConstants.STATUS_SICK;           // 85 ~ 89
        } else if (randValue < 95) {
            return AttendanceStatusConstants.STATUS_EARLY_LEAVE;    // 90 ~ 94
        } else {
            return AttendanceStatusConstants.STATUS_BEREAVEMENT;    // 95 ~ 99
        }
    }

    /**
     * 주어진 출결 상태에 따라 적절한 체크인 시간을 생성합니다.
     *
     * @param status 출결 상태 (예: 출근, 지각, 결근 등)
     * @return 해당 상태에 맞는 체크인 시간, 없으면 null
     */
    private LocalDateTime generateCheckInTimeForStatus(String status) {
        ZoneId zoneId = ZoneId.of("Asia/Seoul");
        LocalDate today = LocalDate.now(zoneId);
        LocalTime baseTime;

        switch (status) {
            case AttendanceStatusConstants.STATUS_PRESENT:
                // 출근: 08:50 ~ 09:10
                baseTime = LocalTime.of(8, 50).plusMinutes(random.nextInt(21));
                break;
            case AttendanceStatusConstants.STATUS_LATE:
                // 지각: 09:11 ~ 10:30
                baseTime = LocalTime.of(9, 11).plusMinutes(random.nextInt(80));
                break;
            case AttendanceStatusConstants.STATUS_OUTING:
                // 외근: 09:00 ~ 10:00
                baseTime = LocalTime.of(9, 0).plusMinutes(random.nextInt(61));
                break;
            case AttendanceStatusConstants.STATUS_EARLY_LEAVE:
                // 반차: 오후 출근 13:00 ~ 14:00
                baseTime = LocalTime.of(13, 0).plusMinutes(random.nextInt(61));
                break;
            case AttendanceStatusConstants.STATUS_VACATION,
                 AttendanceStatusConstants.STATUS_SICK,
                 AttendanceStatusConstants.STATUS_ABSENT,
                 AttendanceStatusConstants.STATUS_BEREAVEMENT:
            default:
                return null; // 출결 기록 없음
        }

        return LocalDateTime.of(today, baseTime);
    }





}