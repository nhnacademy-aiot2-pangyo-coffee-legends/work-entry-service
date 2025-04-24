package com.nhnacademy.workentry.parser.component;

import com.nhnacademy.workentry.parser.dto.AttendanceRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Slf4j
@Component
public class TextParser {
    private static final Pattern DATE_LINE_PATTERN = Pattern.compile("^\\d{4}.*");

    public List<AttendanceRecord> parse(Path txtFile) throws IOException {
        List<AttendanceRecord> records = new ArrayList<>();

        String fileName = txtFile.getFileName().toString();
        String mbName = fileName.substring(0, fileName.lastIndexOf('.'));
        Long mbNo = getMbNo(mbName);

        if (mbNo == null) {
            log.error("❌ '{}' 에 해당하는 사원번호가 없습니다.", mbName);
            return records;
        }

        try (BufferedReader reader = Files.newBufferedReader(txtFile)) {
            String line;

            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.contains("Firefox") || line.matches("^\\d+ /\\d+.*")) {
                    continue;
                }

                // 인코딩 오류 및 날짜 포맷 정제
                line = line.replace("�", ":");
                line = line.replaceAll("(\\d{4}):(\\d{2}):(\\d{2})", "$1-$2-$3");

                // 날짜 라인 여부 확인
                if (!DATE_LINE_PATTERN.matcher(line).find()) {
                    continue;
                }

                // 토큰 분리
                String[] tokens = line.split("\\s+");
                if (tokens.length < 3) {
                    continue;
                }

                String date = tokens[0];
                String checkInStr = tokens[1];
                String checkOutStr = tokens[2];
                String status = tokens.length >= 7 ? tokens[6] : "기타";

                // 날짜/시간 파싱 및 근무시간 계산. "00:00"은 무시 (퇴근 미기록)
                LocalDate workDate = LocalDate.parse(date);
                LocalDateTime checkInTime = null;
                LocalDateTime checkOutTime = null;
                long minutesWorked = 0;

                if (isValidTimeFormat(checkInStr) && !"00:00".equals(checkInStr)) {
                    checkInTime = LocalDateTime.of(workDate, LocalTime.parse(checkInStr, timeFormatter));
                }
                if (isValidTimeFormat(checkOutStr) && !"00:00".equals(checkOutStr)) {
                    checkOutTime = LocalDateTime.of(workDate, LocalTime.parse(checkOutStr, timeFormatter));
                }
                if (checkInTime != null && checkOutTime != null) {
                    minutesWorked = ChronoUnit.MINUTES.between(checkInTime, checkOutTime);
                }

                // 출결 상태 결정: 상태가 한 줄에 없으면 "기타", 그 외는 그대로 유지
                // 기타 상태일 경우 시간 무효화
                if ("기타".equals(status)) {
                    checkInTime = null;
                    checkOutTime = null;
                    minutesWorked = 0;
                }

                records.add(new AttendanceRecord(mbNo, workDate, checkInTime, checkOutTime, minutesWorked, status));
            }

            log.debug("✅ 텍스트 정리가 완료되었습니다.");
        }

        return records;
    }


    private static boolean isValidTimeFormat(String timeStr) {
        return timeStr != null && timeStr.matches("\\d{2}:\\d{2}");
    }

    private static Long getMbNo(String mbName){
        return switch (mbName) {
            case "kyeongyeong" -> 95L;
            case "hyeongho" -> 96L;
            case "inho" -> 97L;
            case "seungeu" -> 98L;
            case "miseong" -> 99L;
            case "noah" -> 100L;
            case "hyeonsup" -> 101L;
            case "yurim" -> 76L;
            default -> null;
        };

    }

}
