package com.nhnacademy.workentry;

import com.nhnacademy.workentry.parser.service.AttendanceConversionService;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootTest
class WorkEntryApplicationTest {

    private static final Path BASE_DIR = Paths.get("src/main/resources/work-log");
    private static final Path TEST_PDF = BASE_DIR.resolve("kyeongyeong.pdf");
    private static final Path OUTPUT_DIR = BASE_DIR.resolve("kyeongyeong");
    private static final Path OUTPUT_CSV = OUTPUT_DIR.resolve("kyeongyeong.csv");

    @Autowired
    AttendanceConversionService conversion;

    @Test
    void integrationTest_pdfToCsvConversion() throws IOException {
        if(Files.exists(OUTPUT_CSV)){
            Files.delete(OUTPUT_CSV);
        }

        Files.createDirectories(OUTPUT_DIR);
        conversion.convert(TEST_PDF, OUTPUT_DIR);

        assertAll(
                ()->assertTrue(Files.exists(OUTPUT_CSV)),
                ()->{
                    String content = Files.readString(OUTPUT_CSV);
                    assertFalse(content.isBlank());
                    assertTrue(content.startsWith("사원번호"));
                }
        );


    }

}
