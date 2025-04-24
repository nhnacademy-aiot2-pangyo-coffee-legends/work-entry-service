package com.nhnacademy.workentry.parser.component;

import com.nhnacademy.workentry.parser.dto.AttendanceRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CsvWriterTest {

    @Autowired
    private TextParser textParser;

    @Autowired
    private CsvWriter csvWriter;

    private static final Path TXT_PATH = Paths.get("src/test/resources/kyeongyeong.txt");
    private static final Path CSV_PATH = Paths.get("src/test/resources/cleaned.csv");
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Test
    void write() throws IOException {
        List<AttendanceRecord> attendanceRecords = textParser.parse(TXT_PATH);

        csvWriter.write(CSV_PATH, attendanceRecords);

        assertAll(
                () -> assertNotNull(attendanceRecords, "출결 데이터가 null입니다."),
                () -> assertTrue(Files.exists(CSV_PATH), "CSV 파일이 생성되지 않았습니다."),
                () -> {
                    String content = Files.readString(CSV_PATH);
                    assertFalse(content.isBlank(), "CSV 파일이 비어 있습니다.");
                    assertTrue(content.startsWith("사원번호,근무일자,입실시간,퇴실시간,근무시간,출결상태"),
                            "CSV 헤더가 올바르지 않습니다.");
                    long lineCount = Files.lines(CSV_PATH).count();
                    assertEquals(attendanceRecords.size() + 1, lineCount, "CSV 라인 수가 일치하지 않습니다."); // +1 for header
                }
        );

        // 첫 레코드의 정확성 검증
        String firstLine;
        try (var lines = Files.lines(CSV_PATH)) {
            firstLine = lines.skip(1).findFirst().orElse("");
        }
        AttendanceRecord first = attendanceRecords.getFirst();

        String expected = String.format("%d,%s,%s,%s,%d,%d",
                first.mbNo(),
                first.workDate(),
                first.checkIn() != null ? first.checkIn().format(FORMATTER) : "NULL",
                first.checkOut() != null ? first.checkOut().format(FORMATTER) : "NULL",
                first.minutesWorked(),
                getStatusCode(first.status())
        );

        assertEquals(expected, firstLine, "첫 레코드가 예상과 일치하지 않습니다.");
    }

    // 테스트 용도용 status 코드 매핑 (CsvWriter와 동일하게 유지)
    private int getStatusCode(String status) {
        return switch (status) {
            case "출석" -> 1;
            case "지각" -> 2;
            case "결석" -> 3;
            case "외출" -> 4;
            case "휴가" -> 5;
            case "질병/입원" -> 6;
            case "조퇴" -> 7;
            default -> 8;
        };
    }
}