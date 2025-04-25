package com.nhnacademy.workentry.parser.component;

import com.nhnacademy.workentry.parser.dto.AttendanceRecord;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@code CsvWriterTest} 클래스는 {@link CsvWriter}의 CSV 파일 생성 기능을 검증하는 통합 테스트 클래스입니다.
 *
 * <p>
 * 본 테스트는 {@link TextParser}를 활용하여 출결 텍스트 파일을 파싱한 후, 그 결과를 {@code CsvWriter}로 CSV 파일로 저장하고
 * 다음의 항목을 검증합니다:
 * </p>
 * <ul>
 *     <li>파싱된 출결 데이터가 null이 아님</li>
 *     <li>CSV 파일이 정상적으로 생성되었는지</li>
 *     <li>CSV 파일 내용이 비어 있지 않으며, 올바른 헤더를 포함하는지</li>
 *     <li>CSV의 전체 줄 수가 출결 데이터 수 + 헤더(1줄)와 일치하는지</li>
 *     <li>첫 번째 출결 레코드가 예상한 포맷과 정확히 일치하는지</li>
 * </ul>
 *
 * <p>
 * 또한, 테스트 코드 내에서 실제 CSV에 기록되는 출결 상태 값(status)을 정수 코드로 매핑하는
 * {@code getStatusCode()} 메서드를 제공하여 {@link CsvWriter}와 동일한 방식으로 비교합니다.
 * </p>
 *
 * <p>
 * 테스트 대상 파일 경로:
 * <ul>
 *     <li>{@code kyeongyeong.txt} : 테스트용 텍스트 출결 파일</li>
 *     <li>{@code cleaned.csv} : 생성될 CSV 파일</li>
 * </ul>
 * </p>
 *
 * @author 김경영
 */
@SpringBootTest
class CsvWriterTest {

    @Autowired
    private TextParser textParser;

    @Autowired
    private CsvWriter csvWriter;

    private static final Path TXT_PATH = Paths.get("src/test/resources/kyeongyeong.txt");
    private static final Path CSV_PATH = Paths.get("src/test/resources/cleaned.csv");
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * {@code write()} 메소드는 실제 출결 텍스트 파일을 파싱한 후,
     * 해당 데이터를 기반으로 CSV 파일을 생성하고 그 정확성을 검증하는 통합 테스트를 수행합니다.
     *
     * <p>다음 항목을 검증합니다:</p>
     * <ul>
     *     <li>파싱된 출결 데이터가 {@code null}이 아님</li>
     *     <li>CSV 파일이 성공적으로 생성되었는지 여부</li>
     *     <li>CSV 파일 내용이 비어 있지 않고, 올바른 헤더를 포함하는지</li>
     *     <li>전체 줄 수가 레코드 수 + 1(헤더 포함)과 일치하는지</li>
     *     <li>CSV 내 첫 번째 레코드 내용이 예상값과 정확히 일치하는지</li>
     * </ul>
     *
     * @throws IOException 파일 입출력 중 오류가 발생할 경우 예외를 던집니다.
     */
    @Test
    @DisplayName("csv파일 생성")
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