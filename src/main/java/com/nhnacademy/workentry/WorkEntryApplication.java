package com.nhnacademy.workentry;

import com.nhnacademy.workentry.parser.service.AttendanceConversionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

@Slf4j
@SpringBootApplication
@RequiredArgsConstructor
public class WorkEntryApplication implements CommandLineRunner {

    private final AttendanceConversionService conversion;

    public static void main(String[] args) {
        SpringApplication.run(WorkEntryApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        Path baseDir = Paths.get("src/main/resources/work-log");

        try (Stream<Path> pdfFiles = Files.walk(baseDir)
                .filter(p -> p.toString().endsWith(".pdf"))) {
            pdfFiles.forEach(pdf -> {
                try {
                    // PDF 파일 이름(확장자 제외) 구하기
                    String name = pdf.getFileName().toString().replaceFirst("\\.pdf$", "");
                    // 하위 폴더(예: .../work-log/yeong) 만들기
                    Path outDir = pdf.getParent().resolve(name);
                    Files.createDirectories(outDir);

                    // TXT 경로: .../yeong/yeong.txt
                    Path txt = outDir.resolve(name + ".txt");
                    conversion.pdfToTxt(pdf, txt);

                    // CSV 경로: .../yeong/yeong.csv
                    Path csv = outDir.resolve(name + ".csv");
                    conversion.txtToCsv(txt, csv);

                    log.debug("✅ 처리완료 : {}", name);
                } catch (IOException e) {
                    log.error("❌ 실패: {} -> {}", pdf.getFileName(), e.getMessage());
                }
            });
        }
    }

}
