package com.nhnacademy.workentry.parser.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * {@code AttendanceRecord}는 개인의 출결 기록 정보를 담는 데이터 전송 객체입니다.
 * <p>
 * 주로 텍스트 파일 파싱 결과를 담아 CSV 파일 생성이나 DB 저장 등의 목적으로 활용됩니다.
 * </p>
 *
 * <p>포함 필드 설명:</p>
 * <ul>
 *     <li>{@code mbNo} - 사원 번호 (회원 고유 식별자)</li>
 *     <li>{@code workDate} - 근무 일자</li>
 *     <li>{@code checkIn} - 입실 시간 (없을 경우 {@code null})</li>
 *     <li>{@code checkOut} - 퇴실 시간 (없을 경우 {@code null})</li>
 *     <li>{@code minutesWorked} - 근무 시간 (분 단위, 입·퇴실 시간 차이)</li>
 *     <li>{@code status} - 출결 상태 (예: 출석, 지각, 결석, 외출 등)</li>
 * </ul>
 *
 * <p>
 * 이 record는 불변성을 보장하므로, 생성 이후 필드 값을 변경할 수 없습니다.
 * </p>
 */
public record AttendanceRecord(
        Long mbNo,
        LocalDate workDate,
        LocalDateTime checkIn,
        LocalDateTime checkOut,
        long minutesWorked,
        String status
) {}
