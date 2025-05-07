package com.nhnacademy.workentry.parser.component;

import com.nhnacademy.workentry.parser.dto.AttendanceRecord;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@code TextParserTest} 클래스는 {@link TextParser} 컴포넌트의 텍스트 파일 파싱 기능을 검증하는 단위 테스트 클래스입니다.
 *
 * <p>
 * 본 테스트는 다음의 기능을 검증하옵니다:
 * </p>
 * <ul>
 *     <li>지정된 경로의 출결 텍스트 파일을 정상적으로 읽어들이는지</li>
 *     <li>{@link AttendanceRecord} 객체 리스트로 올바르게 변환되는지</li>
 *     <li>각 레코드에서 필수 필드(사원번호, 근무일자, 출결상태)가 정상적으로 추출되는지</li>
 * </ul>
 *
 * <p>
 * 테스트는 실제 테스트용 텍스트 파일({@code kyeongyeong.txt})을 기반으로 수행되며,
 * {@code SpringBootTest} 환경에서 {@link TextParser} 빈을 자동 주입받아 실행됩니다.
 * </p>
 *
 * <p>
 * 출력된 결과 리스트가 null이 아니고 비어 있지 않으며, 첫 번째 출결 레코드의 필드 값들이 모두 존재하는지를 확인합니다.
 * </p>
 *
 * @author 김경영
 */
@Slf4j
@SpringBootTest
class TextParserTest {

    @Autowired
    private TextParser textParser;

    /**
     * {@code parse()} 메소드는 지정된 텍스트 파일을 읽어 출결 데이터를 파싱하여 {@link AttendanceRecord} 객체 리스트로 변환하는 기능을 검증하는 테스트 메소드입니다.
     *
     * <p>본 테스트는 {@code kyeongyeong.txt} 파일을 이용하여 다음 사항들을 검증합니다:</p>
     * <ul>
     *     <li>텍스트 파일이 정상적으로 읽혀지는지</li>
     *     <li>파싱된 데이터가 {@link AttendanceRecord} 리스트로 변환되는지</li>
     *     <li>각 레코드의 필수 필드(사원번호, 근무일자, 출결상태)가 정확하게 추출되는지</li>
     *
     * </ul>
     *
     * <p>출력된 리스트가 null이 아니고 비어 있지 않으며, 첫 번째 출결 레코드의 각 필드 값들이 존재하는지 확인합니다.</p>
     *
     * @throws IOException 파일 입출력 중 오류가 발생할 경우 발생합니다.
     */
    @Test
    @DisplayName("출결 텍스트 파일 파싱 테스트")
    void parse() throws IOException {
        Path txtPath = Paths.get("src/test/resources/kyeongyeong.txt");

        List<AttendanceRecord> records = textParser.parse(txtPath);

        log.debug("parse된 records : {}",records);
        assertAll(
                ()->assertNotNull(records, "리턴된 리스트가 null입니다."),
                ()-> assertFalse(records.isEmpty(), "출결 레코드가 비었습니다."),
                ()->{
                    AttendanceRecord record = records.getFirst();
                    assertNotNull(record.mbNo());
                    assertNotNull(record.workDate());
                    assertNotNull(record.status());
                }
        );

    }
}