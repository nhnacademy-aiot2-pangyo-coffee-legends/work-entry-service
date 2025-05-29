package com.nhnacademy.workentry.parser.component;

import com.nhnacademy.workentry.parser.dto.AttendanceRecord;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * {@code CsvWriter} 클래스는 정제된 출결 데이터를 CSV 파일로 변환하는 기능을 담당합니다.
 * <p>
 * 이 클래스는 {@link AttendanceRecord} 목록을 받아,
 * 데이터베이스에 import 가능한 형태의 CSV 파일을 생성합니다.
 * </p>
 */
@Component
public class CsvWriter {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * 출결 데이터를 기반으로 CSV 파일을 생성합니다.
     * <p>
     * 파일 첫 줄에는 CSV 헤더가 작성되며, 이후 각 레코드는 한 줄씩 기록됩니다.
     *
     * @param csvFile 생성할 CSV 파일 경로
     * @param records 변환 대상 출결 데이터 목록
     * @throws IOException 파일 쓰기 중 I/O 오류 발생 시
     */
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

    /**
     * 출결 상태 문자열을 정수 코드로 변환합니다.
     * @param status
     * @return
     */
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
