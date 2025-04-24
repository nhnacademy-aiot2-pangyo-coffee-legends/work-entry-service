package com.nhnacademy.workentry.parser.component;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

@Slf4j
@Component
public class PdfExtractor {
    public void extract(Path pdfFile, Path textFile) {
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
}
