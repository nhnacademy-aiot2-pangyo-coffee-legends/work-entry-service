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

/**
 * {@code WorkEntryApplication} 클래스는 Spring Boot 기반 출결 변환 애플리케이션의 진입점 클래스입니다.
 *
 * <p>
 * {@link CommandLineRunner}를 구현하여, 애플리케이션 실행 시 자동으로 출결 PDF 파일들을 스캔하고
 * 변환하는 로직을 수행합니다.
 * </p>
 *
 * <p>
 * 애플리케이션은 다음과 같은 순서로 작업을 수행합니다:
 * </p>
 * <ol>
 *     <li>{@code src/main/resources/work-log} 디렉토리 하위의 모든 PDF 파일 탐색</li>
 *     <li>각 PDF 파일에 대해, 해당 파일명과 동일한 이름의 출력 디렉토리 생성</li>
 *     <li>{@link AttendanceConversionService}를 통해 PDF → TXT → CSV 변환 수행</li>
 *     <li>성공 및 실패 로그 출력</li>
 * </ol>
 *
 * <p>
 * 실패한 경우 {@code log.error}로 파일명과 예외 메시지를 출력하며, 나머지 파일 처리는 계속 진행됩니다.
 * </p>
 *
 * @author 김경영
 */
@Slf4j
@SpringBootApplication
@RequiredArgsConstructor
public class WorkEntryApplication implements CommandLineRunner {

    private final AttendanceConversionService conversion;

    /**
     * Spring Boot 애플리케이션의 시작점입니다.
     *
     * @param args 커맨드라인 인자
     */
    public static void main(String[] args) {
        SpringApplication.run(WorkEntryApplication.class, args);
    }

    /**
     * 애플리케이션 실행 시 호출되어 PDF 파일들을 자동으로 변환합니다.
     *
     * @param args 커맨드라인 인자
     * @throws Exception 파일 처리 중 예외가 발생할 수 있음
     */
    @Override
    public void run(String... args) throws Exception {
        Path baseDir = Paths.get("src/main/resources/work-log");

        try (Stream<Path> pdfFiles = Files.walk(baseDir)
                .filter(Files::isRegularFile)
                .filter(p -> p.toString().endsWith(".pdf"))) {
            pdfFiles.forEach(pdf -> {
                try {
                    // PDF 파일 이름(확장자 제외) 구하기
                    String name = pdf.getFileName().toString().replaceFirst("\\.pdf$", "");
                    // 하위 폴더(예: .../work-log/kyeongyeong) 만들기
                    Path outDir = pdf.getParent().resolve(name);
                    Files.createDirectories(outDir);

                    conversion.convert(pdf, outDir);

                    log.debug("✅ 처리완료 : {}", name);
                } catch (IOException e) {
                    log.error("❌ 실패: {}", pdf.getFileName(), e);
                }
            });
        }
    }

}
