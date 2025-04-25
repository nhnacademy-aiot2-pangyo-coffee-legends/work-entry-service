package com.nhnacademy.workentry.parser.component;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@code PdfExtractorTest} 클래스는 {@link PdfExtractor} 컴포넌트의 PDF → 텍스트 변환 기능을 검증하는 단위 테스트 클래스입니다.
 *
 * <p>
 * 테스트는 다음과 같은 절차로 구성됩니다:
 * </p>
 * <ol>
 *     <li>테스트용 PDF 파일을 지정된 경로에서 읽습니다.</li>
 *     <li>{@code extract()} 메서드를 호출하여 텍스트 파일로 변환합니다.</li>
 *     <li>텍스트 파일이 실제로 생성되었는지, 또한 내용이 비어 있지 않은지를 검증합니다.</li>
 *     <li>테스트 종료 후, 생성된 텍스트 파일을 삭제하여 클린업(clean-up)합니다.</li>
 * </ol>
 *
 * <p>
 * 테스트 대상 파일 경로:
 * <ul>
 *     <li>{@code kyeongyeong.pdf} : 입력 PDF 파일</li>
 *     <li>{@code output-pdf.txt} : 출력 텍스트 파일</li>
 * </ul>
 * </p>
 *
 * <p>
 * 본 테스트는 {@code SpringBootTest} 어노테이션을 활용하여 {@link PdfExtractor} 빈을 자동 주입받아 실행되며,
 * 실제 파일 입출력을 동반하므로 테스트 환경에서 해당 경로의 파일 존재 여부를 사전에 확인해야 합니다.
 * </p>
 *
 * @author 김경영
 */
@SpringBootTest
class PdfExtractorTest {

    @Autowired
    private PdfExtractor pdfExtractor;

    /**
     * {@code extract()} 메소드는 지정된 PDF 파일을 텍스트 파일로 변환한 후,
     * 변환된 텍스트 파일이 정상적으로 생성되었고 내용이 비어 있지 않은지를 검증하는 테스트 메소드입니다.
     *
     * <p>테스트 절차:</p>
     * <ol>
     *     <li>{@code kyeongyeong.pdf} 파일을 {@link PdfExtractor}를 통해 변환합니다.</li>
     *     <li>결과 파일 {@code output-pdf.txt}가 존재하는지 검증합니다.</li>
     *     <li>변환된 텍스트 내용이 공백이 아님을 확인합니다.</li>
     *     <li>테스트 후 해당 텍스트 파일을 삭제하여 파일 시스템을 정리합니다.</li>
     * </ol>
     *
     * <p>본 테스트는 실제 디스크 I/O를 수행하므로, 테스트 리소스 파일이 미리 준비되어 있어야 합니다.</p>
     *
     * @throws IOException 파일 읽기 또는 쓰기 중 오류가 발생할 경우 발생합니다.
     */
    @Test
    @DisplayName("pdf파일 텍스트파일로 변환")
    void extract() throws IOException{
        Path pdfPath = Paths.get("src/test/resources/kyeongyeong.pdf");
        Path textPath = Paths.get("src/test/resources/output-pdf.txt");

        pdfExtractor.extract(pdfPath, textPath);

        assertAll(
                ()->{
                    assertTrue(Files.exists(textPath), "텍스트 파일이 생성되지 않았습니다.");
                    String content = Files.readString(textPath);
                    assertFalse(content.isBlank(), "변환된 텍스트 내용이 비어 있습니다.");
                }
        );

        Files.deleteIfExists(textPath);
    }
}