package com.nhnacademy.workentry.attendance.controller;

import com.nhnacademy.workentry.attendance.bot.AttendanceSimulatorBot;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 테스트용 출결 시뮬레이터 컨트롤러입니다.
 * 실제 배포 시에는 삭제 또는 접근 제한이 필요합니다.
 */
@RestController
@RequiredArgsConstructor
public class AttendanceBotController {

    private final AttendanceSimulatorBot simulatorBot;

    @PostMapping("/simulate/check-in")
    public String simulateCheckIn() {
        simulatorBot.createCheckInAttendanceData();
        return "Check-in simulation complete.";
    }

    @PostMapping("/simulate/check-out")
    public String simulateCheckOut() {
        simulatorBot.createCheckOutAttendanceData();
        return "Check-out simulation complete.";
    }
}
