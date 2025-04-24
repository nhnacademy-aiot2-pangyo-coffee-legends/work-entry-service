package com.nhnacademy.workentry.parser.component;

import com.nhnacademy.workentry.parser.dto.AttendanceRecord;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class CsvWriter {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public void write(Path csvFile, List<AttendanceRecord> records) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(csvFile)) {
            writer.write("사원번호,근무일자,입실시간,퇴실시간,근무시간,출결상태");
            writer.newLine();

            for (AttendanceRecord record : records) {
                writer.write(String.format("%d,%s,%s,%s,%d,%d",
                        record.mbNo(),
                        record.workDate(),
                        record.checkIn() != null ? record.checkIn().format(FORMATTER) : "NULL",
                        record.checkOut() != null ? record.checkOut().format(FORMATTER) : "NULL",
                        record.minutesWorked(),
                        getStatusCode(record.status())
                ));
                writer.newLine();
            }
        }
    }

    private int getStatusCode(String status) {
        return switch (status) {
            case "출석" -> 1;
            case "지각" -> 2;
            case "결석" -> 3;
            case "외출" -> 4;
            case "휴가" -> 5;
            case "질병/입원" -> 6;
            case "조퇴" -> 7;
            default -> 8; // 기타
        };
    }
}
