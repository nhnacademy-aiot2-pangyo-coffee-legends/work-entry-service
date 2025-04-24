package com.nhnacademy.workentry.parser.component;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PdfExtractorTest {

    @Autowired
    private PdfExtractor pdfExtractor;

    @Test
    void extract() throws IOException{
        Path pdfPath = Paths.get("src/test/resources/sample-pdf.pdf");
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