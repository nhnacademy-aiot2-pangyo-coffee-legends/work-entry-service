package com.nhnacademy.workentry.attendance.controller;

import com.nhnacademy.workentry.attendance.bot.AttendanceSimulatorBot;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 로컬 개발 환경에서만 활성화되는 출결 시뮬레이터 컨트롤러입니다.
 * <p>
 * {@code @Profile("local")} 설정에 따라, 프로덕션 환경에서는 빈으로 등록되지 않습니다.
 * 테스트 목적의 시뮬레이션용이며, 배포 시에는 포함되지 않습니다.
 */
@Profile("local")
@RestController
@RequiredArgsConstructor
class AttendanceBotController {

    private final AttendanceSimulatorBot simulatorBot;

    @PostMapping("/simulate/check-in")
    private String simulateCheckIn() {
        simulatorBot.createCheckInAttendanceData();
        return "Check-in simulation complete.";
    }

    @PostMapping("/simulate/check-out")
    private String simulateCheckOut() {
        simulatorBot.createCheckOutAttendanceData();
        return "Check-out simulation complete.";
    }
}
