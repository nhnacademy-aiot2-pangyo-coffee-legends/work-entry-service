package com.nhnacademy.workentry.parser.component;

import com.nhnacademy.workentry.parser.dto.AttendanceRecord;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class TextParserTest {

    @Autowired
    private TextParser textParser;

    @Test
    void parse() throws IOException {
        Path txtPath = Paths.get("src/test/resources/kyeongyeong.txt");

        List<AttendanceRecord> records = textParser.parse(txtPath);

        log.debug("parse된 records : {}",records);
        assertAll(
                ()->assertNotNull(records, "리턴된 리스트가 null입니다."),
                ()-> assertFalse(records.isEmpty(), "출결 레코드가 비었습니다."),
                ()->{
                    AttendanceRecord record = records.getFirst();
                    assertNotNull(record.workDate());
                    assertNotNull(record.status());
                }
        );

    }
}