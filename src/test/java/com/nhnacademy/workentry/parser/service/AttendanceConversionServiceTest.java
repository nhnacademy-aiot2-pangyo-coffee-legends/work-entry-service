package com.nhnacademy.workentry.parser.service;

import com.nhnacademy.workentry.parser.component.CsvWriter;
import com.nhnacademy.workentry.parser.component.PdfExtractor;
import com.nhnacademy.workentry.parser.component.TextParser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@code AttendanceConversionServiceTest} 클래스는 {@link AttendanceConversionService}의 PDF → CSV 변환 기능을 검증하는 통합 테스트 클래스.
 *
 * <p>
 * 본 테스트 클래스는 다음 컴포넌트들이 정상적으로 협업하여 하나의 작업 흐름을 수행하는지를 검증하옵니다:
 * </p>
 * <ul>
 *     <li>{@link PdfExtractor} : PDF 파일을 텍스트 파일로 추출</li>
 *     <li>{@link TextParser} : 텍스트 파일을 파싱하여 {@code AttendanceRecord} 리스트로 변환</li>
 *     <li>{@link CsvWriter} : 변환된 리스트를 CSV 형식으로 출력</li>
 * </ul>
 *
 * <p>
 * 테스트는 실제 PDF 파일을 입력으로 하여 변환 과정을 수행하고, 최종적으로 생성된 CSV 파일의 존재 여부 및 내용의 유효성을 검증
 * </p>
 *
 * @author 김경영
 */
@SpringBootTest
class AttendanceConversionServiceTest {

    @Autowired
    private PdfExtractor pdfExtractor;

    @Autowired
    private TextParser textParser;

    @Autowired
    private CsvWriter csvWriter;

    @Autowired
    private AttendanceConversionService attendanceConversionService;

    private static final Path PDF_PATH = Paths.get("src/test/resources/kyeongyeong.pdf");
    private static final Path CSV_PATH = Paths.get("src/test/resources/kyeongyeong/kyeongyeong.csv");

    /**
     * {@code convert()} 메소드는 {@link AttendanceConversionService#convert(Path, Path)} 기능이 정상적으로 작동하는지를 검증합니다.
     *
     * <p>
     * PDF 파일을 지정된 디렉토리로 변환할 때:
     * </p>
     * <ul>
     *     <li>CSV 파일이 실제로 생성되었는지</li>
     *     <li>생성된 CSV 파일이 비어 있지 않은지</li>
     * </ul>
     * <p>
     * 위의 조건을 만족하는지를 검증하여 변환 흐름이 올바르게 구성되었는지를 확인합니다.
     * </p>
     *
     * @throws IOException 변환 과정 중 파일 입출력 오류가 발생할 경우
     */
    @Test
    void convert() throws IOException {

        String name = PDF_PATH.getFileName().toString().replaceFirst("\\.pdf$", "");
        Path outDir = PDF_PATH.getParent().resolve(name);

        attendanceConversionService.convert(PDF_PATH, outDir);

        assertAll(
                ()->assertTrue(Files.exists(CSV_PATH)),
                ()->{
                    String content = Files.readString(CSV_PATH);
                    assertFalse(content.isBlank());
                }

        );
    }
}