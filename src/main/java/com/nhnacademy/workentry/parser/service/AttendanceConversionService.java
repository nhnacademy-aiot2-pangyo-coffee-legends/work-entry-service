package com.nhnacademy.workentry.parser.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.regex.Pattern;

@Slf4j
@Service
public class AttendanceConversionService {

    public void pdfToTxt(Path pdfFile, Path textFile) throws IOException {

        try {
            // PDF 문서 열기
            PDDocument document = PDDocument.load(new File(pdfFile.toString()));

            // PDF 문서에서 텍스트 추출하기
            PDFTextStripper stripper = new PDFTextStripper();

            stripper.setStartPage(2); // 시작 페이지
            stripper.setEndPage(document.getNumberOfPages()); // 마지막 페이지 - 시작과 마지막을 정해줘야 원하는 페이지를 추출할 수 있음

            String text = stripper.getText(document);

            // 추출된 텍스트를 텍스트 파일로 저장하기
            FileWriter writer = new FileWriter(textFile.toString());
            writer.write(text);
            writer.close();

            // PDF 문서 닫기
            document.close();

            log.debug("PDF 파일이 텍스트로 변환되어 저장되었습니다.");

        } catch (IOException e) {
            log.error("오류 발생: {}", e.getMessage());
        }
    }

    public void txtToCsv(Path txtFile, Path csvFile) throws IOException {

        try (BufferedReader reader = Files.newBufferedReader(txtFile);
             BufferedWriter writer = Files.newBufferedWriter(csvFile)) {

            // CSV 헤더 작성
            writer.write("근무일자,입실시간,퇴실시간,근무시간,출결상태");
            writer.newLine();

            String line;
            Pattern dateLinePattern = Pattern.compile("^\\d{4}.*");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.contains("Firefox") || line.matches("^\\d+ /\\d+.*")) {
                    continue;
                }

                // 인코딩 오류 및 날짜 포맷 정제
                line = line.replace("�", ":");
                line = line.replaceAll("(\\d{4}):(\\d{2}):(\\d{2})", "$1-$2-$3");

                // 날짜 라인 여부 확인
                if (!dateLinePattern.matcher(line).find()) {
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
                String status;
                if (tokens.length >= 7) {
                    status = tokens[6];
                } else {
                    status = "기타";
                    // 입/퇴실 미기록 상태이므로 시간 필드 null, 분 0
                    checkInTime = null;
                    checkOutTime = null;
                    minutesWorked = 0;
                }

                // CSV 출력
                writer.write(String.format(
                        "%s,%s,%s,%d,%s",
                        date,
                        checkInTime != null ? checkInTime.format(dateTimeFormatter) : "NULL",
                        checkOutTime != null ? checkOutTime.format(dateTimeFormatter) : "NULL",
                        minutesWorked,
                        status
                ));
                writer.newLine();
            }

            log.debug("✅ 텍스트 정리가 완료되었습니다.");
        }
    }

    private static boolean isValidTimeFormat(String timeStr) {
        return timeStr != null && timeStr.matches("\\d{2}:\\d{2}");
    }
}
