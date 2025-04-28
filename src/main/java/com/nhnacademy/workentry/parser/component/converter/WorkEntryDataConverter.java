package com.nhnacademy.workentry.parser.component.converter;

import com.nhnacademy.workentry.parser.service.AttendanceConversionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * {@code WorkEntryDataConverter} 클래스는 로컬에 저장된 출결 PDF 파일들을 일괄 변환하는 기능을 담당합니다.
 * <p>
 * 지정된 디렉토리 아래에서 PDF 파일을 찾아, 각 파일에 대해 텍스트 추출 및 CSV 변환 작업을 수행합니다.
 * 이 클래스는 {@link AttendanceConversionService}를 이용하여 실제 변환 작업을 위임합니다.
 * </p>
 *
 * <p>
 * 변환된 결과는 PDF 파일명과 동일한 하위 폴더에 저장됩니다.
 * </p>
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class WorkEntryDataConverter {
    private final AttendanceConversionService conversion;

    /**
     * 작업 디렉토리 내 모든 PDF 파일을 찾아 텍스트 및 CSV 파일로 변환합니다.
     *
     * <p>
     * 기본 디렉토리는 {@code src/main/resources/work-log}로 설정되어 있으며,
     * 각 PDF 파일에 대해 전용 폴더를 생성하고 그 안에 변환된 결과 파일(.txt, .csv)을 저장합니다.
     * </p>
     *
     * @throws IOException 파일 생성 또는 읽기/쓰기 과정 중 오류가 발생할 경우 예외가 발생합니다.
     */
    public void convertWorkLogs() throws IOException {
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

                    // 변환 수행
                    conversion.convert(pdf, outDir);

                    log.debug("✅ 처리완료 : {}", name);
                } catch (IOException e) {
                    log.error("❌ 실패: {} -> {}", pdf.getFileName(), e.getMessage());
                }
            });
        }
    }
}
