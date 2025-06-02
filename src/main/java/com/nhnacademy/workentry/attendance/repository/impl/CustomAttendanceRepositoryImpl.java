package com.nhnacademy.workentry.attendance.repository.impl;

import com.nhnacademy.workentry.adapter.member.dto.MemberNoResponse;
import com.nhnacademy.workentry.attendance.dto.AttendanceDto;
import com.nhnacademy.workentry.attendance.dto.QAttendanceDto;
import com.nhnacademy.workentry.attendance.entity.QAttendance;
import com.nhnacademy.workentry.attendance.repository.CustomAttendanceRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * {@link CustomAttendanceRepository}의 QueryDSL 기반 사용자 정의 구현체입니다.
 *
 * <p>회원의 출결 내역을 기간별로 조회하며, 출결 날짜(workDate)는 {@link java.time.LocalDate} 기준입니다.
 * {@link com.nhnacademy.workentry.attendance.dto.AttendanceDto} 형태로 페이징된 데이터를 반환합니다.</p>
 *
 * <p>이 구현은 JPAQueryFactory를 사용하여 직접 SQL을 생성함으로써, 복잡한 조건의 효율적인 조회가 가능합니다.</p>
 */
@Repository
@RequiredArgsConstructor
public class CustomAttendanceRepositoryImpl implements CustomAttendanceRepository {

    private final JPAQueryFactory queryFactory;

    /**
     * 지정된 회원 번호와 기간에 해당하는 출결 내역을 조회합니다.
     *
     * <p>출결 기준은 출근일(workDate)이며, 시간대(LocalDate)의 날짜 부분만 비교하여
     * 해당 날짜 범위에 포함된 출결 정보만 조회합니다.</p>
     *
     * @param mbNo 회원 고유 번호
     * @param start 조회 시작 일시 (LocalDate, 날짜 기준 비교)
     * @param end 조회 종료 일시 (LocalDate, 날짜 기준 비교)
     * @param pageable 페이징 정보
     * @return 지정된 조건에 맞는 출결 정보 DTO의 페이지 객체
     */
    @Override
    public Page<AttendanceDto> getAttendanceByNoAndDateRange(Long mbNo, LocalDate start, LocalDate end, Pageable pageable) {
        QAttendance attendance = QAttendance.attendance;

        List<AttendanceDto> content = queryFactory
                .select(
                        new QAttendanceDto(
                            attendance.id,
                            attendance.mbNo,
                            attendance.workDate,
                            attendance.inTime,
                            attendance.outTime,
                            attendance.status.description
                        )
                )
                .from(attendance)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = Optional.ofNullable(
                queryFactory
                        .select(attendance.count())
                        .from(attendance)
                        .where(
                                attendance.mbNo.eq(mbNo),
                                attendance.workDate.between(start, end)
                        )
                        .fetchOne()
        ).orElse(0L);

        return new PageImpl<>(content, pageable, total);
    }

    /**
     * 오늘 날짜에 체크인한 멤버들의 목록을 조회합니다.
     * 출근 레코드가 아예 없으면 주말로 간주되어 결과는 빈 리스트가 됩니다.
     *
     * @param date 조회할 날짜
     * @return 체크인한 멤버 번호 리스트
     */
    @Override
    public List<MemberNoResponse> getCheckedInMembers(LocalDate date) {
        QAttendance attendance = QAttendance.attendance;

        return queryFactory
                .select(Projections.constructor(MemberNoResponse.class,
                attendance.mbNo))
                .from(attendance)
                .where(
                        attendance.workDate.eq(date),
                        attendance.inTime.isNotNull()
                )
                .groupBy(attendance.mbNo)
                .fetch();
    }
}
