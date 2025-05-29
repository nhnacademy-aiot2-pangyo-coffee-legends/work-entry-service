package com.nhnacademy.workentry.parser.component;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

/**
 * {@code PdfExtractor} 클래스는 PDF 파일로부터 텍스트를 추출하여
 * 지정된 텍스트 파일로 저장하는 역할을 수행합니다.
 * <p>
 * 이 클래스는 {@link PDDocument}와 {@link PDFTextStripper}를 활용하여,
 * PDF 문서의 일부 페이지를 텍스트로 변환한 뒤 파일로 저장합니다.
 * </p>
 */
@Slf4j
@Component
public class PdfExtractor {

    /**
     * 지정된 PDF 파일로부터 텍스트를 추출하여 텍스트 파일로 저장합니다.
     * <p>
     * PDF의 2페이지부터 마지막 페이지까지의 내용을 추출합니다.
     *
     * @param pdfFile  텍스트를 추출할 PDF 파일 경로
     * @param textFile 추출된 텍스트를 저장할 출력 파일 경로
     */
    public void extract(Path pdfFile, Path textFile) {
        try {
            PDDocument document = PDDocument.load(new File(pdfFile.toString()));

            PDFTextStripper stripper = new PDFTextStripper();

            stripper.setStartPage(2); // 시작 페이지
            stripper.setEndPage(document.getNumberOfPages()); // 마지막 페이지 - 시작과 마지막을 정해줘야 원하는 페이지를 추출할 수 있음

            String text = stripper.getText(document);

            FileWriter writer = new FileWriter(textFile.toString());
            writer.write(text);
            writer.close();

            document.close();

            log.debug("PDF 파일이 텍스트로 변환되어 저장되었습니다.");

        } catch (IOException e) {
            log.error("오류 발생: {}", e.getMessage());
        }
    }
}
