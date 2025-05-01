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

/**
 * {@code TextParser} 클래스는 PDF에서 추출된 텍스트 데이터를 정제하여
 * {@link AttendanceRecord} 객체 리스트로 변환하는 역할을 담당합니다.
 * <p>
 * 이 클래스는 텍스트 파일에서 유효한 출결 정보만을 필터링 및 파싱하여,
 * 데이터베이스 입력 혹은 CSV 파일 생성을 위한 중간 데이터 형태로 가공합니다.
 * </p>
 * <p>
 * 주요 기능:
 * <ul>
 *     <li>출결 상태, 입실/퇴실 시간 등의 필드 파싱</li>
 *     <li>근무 시간 계산</li>
 *     <li>이상 데이터 및 인코딩 오류 정제</li>
 * </ul>
 */
@Slf4j
@Component
public class TextParser {
    private static final Pattern DATE_LINE_PATTERN = Pattern.compile("^\\d{4}.*");

    /**
     * "HH:mm" 형식의 유효한 시간 문자열인지 검증합니다.
     *
     * @param timeStr 검증할 시간 문자열
     * @return 유효하면 {@code true}, 그렇지 않으면 {@code false}
     */
    private static boolean isValidTimeFormat(String timeStr) {
        return timeStr != null && timeStr.matches("\\d{2}:\\d{2}");
    }

    /**
     * 이름 문자열을 기반으로 사원번호를 반환합니다.
     *
     * @param mbName 사원의 이름 문자열
     * @return 사원번호, 없을 경우 {@code null}
     */
    private static Long getMbNo(String mbName) {
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

    /**
     * 지정된 텍스트 파일로부터 출결 정보를 추출하여 {@code AttendanceRecord} 객체의 리스트로 반환합니다.
     * <p>
     * 날짜 형식 정제, 시간 형식 검증, 근무 시간 계산, 출결 상태 판단 등의 과정을 수행합니다.
     *
     * @param txtFile 정제할 텍스트 파일의 경로
     * @return 출결 정보가 담긴 {@link AttendanceRecord} 리스트
     * @throws IOException 파일 읽기 실패 시 발생
     */
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


}
