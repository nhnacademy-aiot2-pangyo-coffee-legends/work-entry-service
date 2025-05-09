package com.nhnacademy.workentry.bot;

import com.nhnacademy.workentry.parser.dto.AttendanceRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;

@RequiredArgsConstructor
public class AttendanceSimulatorBot {
    private AttendanceRecord attendanceRecord;

    public

    @Scheduled
    public AttendanceRecord createAttendanceData(){
        return new AttendanceRecord()
    }

}
