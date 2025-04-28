package com.nhnacademy.workentry.parser.service;

import com.nhnacademy.workentry.parser.component.CsvWriter;
import com.nhnacademy.workentry.parser.component.PdfExtractor;
import com.nhnacademy.workentry.parser.component.TextParser;
import com.nhnacademy.workentry.parser.dto.AttendanceRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Path;
import java.util.List;

/**
 * {@code AttendanceConversionService}는 출결 PDF 파일을 텍스트 파일로 추출하고,
 * 이를 정제하여 CSV 파일로 변환하는 전체 과정을 담당하는 서비스 클래스입니다.
 *
 * <p>
 * 이 클래스는 {@link PdfExtractor}, {@link TextParser}, {@link CsvWriter}의 세 컴포넌트를 조합하여
 * 하나의 변환 흐름을 구성합니다. 입력된 PDF 파일 경로를 기준으로 다음 절차를 수행합니다:
 * </p>
 *
 * <ol>
 *     <li>PDF에서 텍스트 추출하여 .txt 파일 생성</li>
 *     <li>텍스트 파일을 파싱하여 {@link AttendanceRecord} 리스트로 정제</li>
 *     <li>출결 데이터를 CSV 파일로 저장</li>
 * </ol>
 *
 * <p>
 * 해당 클래스는 {@code @Service}로 등록되어 있으며, 스프링 컨테이너에 의해 관리됩니다.
 * 외부에서는 {@link #convert(Path, Path)} 메소드를 호출하여 출결 데이터 변환을 수행할 수 있습니다.
 * </p>
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class AttendanceConversionService {

    private final PdfExtractor pdfExtractor;
    private final TextParser textParser;
    private final CsvWriter csvWriter;

    /**
     * 주어진 PDF 파일을 텍스트 및 CSV 파일로 변환합니다.
     *
     * @param pdfPath 변환 대상 PDF 파일 경로
     * @param outDir 출력 파일(.txt, .csv)을 저장할 디렉토리 경로
     * @throws IOException 파일 생성 또는 읽기/쓰기 중 오류 발생 시 예외 발생
     */
    public void convert(Path pdfPath, Path outDir) throws IOException {
        String name = pdfPath.getFileName().toString().replaceFirst("\\.pdf$", "");
        Path txtPath = outDir.resolve(name + ".txt");
        Path csvPath = outDir.resolve(name + ".csv");

        pdfExtractor.extract(pdfPath, txtPath);

        List<AttendanceRecord> records = textParser.parse(txtPath);

        csvWriter.write(csvPath, records);

    }

}
