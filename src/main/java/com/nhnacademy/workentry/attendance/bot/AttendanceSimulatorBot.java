package com.nhnacademy.workentry.attendance.bot;

import com.nhnacademy.workentry.adapter.member.client.MemberServiceClient;
import com.nhnacademy.workentry.adapter.member.dto.MemberNoResponse;
import com.nhnacademy.workentry.attendance.constant.AttendanceStatusConstants;
import com.nhnacademy.workentry.attendance.dto.AttendanceRequest;
import com.nhnacademy.workentry.attendance.service.AttendanceService;
import com.nhnacademy.workentry.common.exception.AttendanceNotFoundException;
import com.nhnacademy.workentry.common.exception.MemberNotFoundException;
import feign.FeignException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class AttendanceSimulatorBot {
    private final MemberServiceClient memberServiceClient;
    private final AttendanceService attendanceService;

    private final Random random = new Random();

    /**
     * 매일 오전 9시에 실행되어 출근 출결 데이터를 생성합니다.
     *
     * 평일에는 일반적인 출근 상태를 기반으로 데이터를 생성하며,
     * 주말(토요일, 일요일)에는 낮은 확률로 출근 상태를 결정합니다.
     * 각 멤버에 대해 출결 요청을 생성하고 AttendanceService에 전달합니다.
     */
    @Scheduled(cron = "0 0 9 * * *", zone = "Asia/Seoul")
    public void createCheckInAttendanceData() {
        log.info("오전 스케줄 실행: {}", LocalDateTime.now());
        List<MemberNoResponse> memberIds;
        try{
            memberIds = memberServiceClient.getAllMemberIds();
        } catch(FeignException.NotFound e){
            throw new MemberNotFoundException("FeignClient : 멤버 정보를 찾을 수 없습니다.");
        }

        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        log.info("오늘 날짜 확인 : {}", LocalDateTime.now());

        for (MemberNoResponse mbNo : memberIds) {
            String status;
            int dayCode = getDateDayCode(today.toString());
            if(dayCode == 1 || dayCode == 7){
                status = decideWeekendAttendanceStatus();
                if(status == null){
                    log.info("주말 비출근 처리 : memberNo = {}", mbNo.getMbNo());
                    continue; // 기록 생성 생략
                }
            } else {
                status = decideAttendanceStatus();
            }

            try {
                // 새로운 출근 레코드 생성
                log.debug("check-in 생성 시도: memberNo={}, status={}, checkInTime={}", mbNo.getMbNo(), status, generateCheckInTimeForStatus(status));

                attendanceService.createAttendance(
                        new AttendanceRequest(mbNo.getMbNo(), today, generateCheckInTimeForStatus(status), null, null, status)
                );
            } catch (Exception e) {
                log.error("❌ 출근 데이터 생성 실패: memberNo={}, status={}, error={}",
                        mbNo.getMbNo(), status, e.getMessage(), e);
            }
        }
    }

    /**
     * 매일 오후 6시에 체크아웃 데이터를 생성합니다.
     *
     * <p>
     * 오늘 날짜에 체크인 기록이 존재하는 멤버들만 대상으로 체크아웃을 수행합니다.<br>
     * 주말에는 출결 레코드 자체가 생성되지 않으므로 체크아웃도 생략됩니다.
     * </p>
     */
    @Scheduled(cron = "0 0 18 * * *", zone = "Asia/Seoul")
    public void createCheckOutAttendanceData() {
        log.info("오후 스케줄 실행: {}", LocalDateTime.now());
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        List<MemberNoResponse> checkedInMembers = attendanceService.getCheckedInMembers(today);

        if(checkedInMembers.isEmpty()){
            log.info("오늘({}) 체크인한 멤버 없음 - 주말 또는 공휴일", today);
            return;
        }

        for (MemberNoResponse mbNo : checkedInMembers) {
            try{
                attendanceService.checkOut(mbNo.getMbNo(), today);
            }catch (AttendanceNotFoundException e){
                log.warn("체크인 기록 없음: memberNo={}, date={}", mbNo.getMbNo(), today);
            }
        }
    }

    /**
     * 랜덤한 출결 상태를 결정하는 메소드 입니다.
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
     * 주말 출근 여부를 확률적으로 결정합니다.
     *
     * 주말 출근은 드문 상황으로 간주되므로,
     * 약 20% 확률로 출근 상태를 반환하고,
     * 나머지 경우에는 null을 반환하여 출근하지 않음을 나타냅니다.
     *
     * @return 출근 상태 상수(출근 시) 또는 null(비출근 시)
     */
    private String decideWeekendAttendanceStatus(){
        int randValue = (int) (Math.random() * 100); // 0~99

        if (randValue < 20) {
            return AttendanceStatusConstants.STATUS_PRESENT; // 0 ~ 19
        } else {
            return null;
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

    /**
     * 입력된 날짜의 요일 코드를 반환합니다.
     *
     * Calendar 기준으로 일요일은 1, 토요일은 7입니다.
     * 이를 통해 주말(1: 일요일, 7: 토요일)과 평일을 구분할 수 있습니다.
     *
     * @param date "yyyy-MM-dd" 형식의 날짜 문자열
     * @return 요일 코드 (1~7)
     * @throws RuntimeException 날짜 파싱에 실패한 경우 예외 발생
     */
    private Integer getDateDayCode(String date){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try{
            Date parseDate = dateFormat.parse(date);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(parseDate);

            return calendar.get(Calendar.DAY_OF_WEEK);

        } catch (ParseException e) {
            throw new RuntimeException("얍얍~");
        }

    }



}