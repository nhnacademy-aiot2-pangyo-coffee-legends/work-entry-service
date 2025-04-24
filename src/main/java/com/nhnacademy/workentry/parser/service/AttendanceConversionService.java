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

@Slf4j
@RequiredArgsConstructor
@Service
public class AttendanceConversionService {

    private final PdfExtractor pdfExtractor;
    private final TextParser textParser;
    private final CsvWriter csvWriter;

    public void convert(Path pdfPath, Path outDir) throws IOException {
        String name = pdfPath.getFileName().toString().replaceFirst("\\.pdf$", "");
        Path txtPath = outDir.resolve(name + ".txt");
        Path csvPath = outDir.resolve(name + ".csv");

        pdfExtractor.extract(pdfPath, txtPath);

        List<AttendanceRecord> records = textParser.parse(txtPath);

        csvWriter.write(csvPath, records);
    }


}
