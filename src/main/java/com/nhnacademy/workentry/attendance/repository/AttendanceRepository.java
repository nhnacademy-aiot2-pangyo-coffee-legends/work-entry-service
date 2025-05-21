package com.nhnacademy.workentry.attendance.repository;

import com.nhnacademy.workentry.attendance.entity.Attendance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 출결 정보를 관리하는 JPA Repository입니다.
 * <p>
 * 페이지네이션 지원을 위해 {@link Pageable} 기반 메서드를 정의합니다.
 */
public interface AttendanceRepository extends JpaRepository<Attendance, Long>, CustomAttendanceRepository{

    // 회원 번호로 전체 출결 기록 조회
    List<Attendance> findAllByMbNo(Long mbNo);

    // 날짜 범위 기반 전체 출결 데이터 조회 (페이지네이션)
    Page<Attendance> findByWorkDateBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);

    //특정 회원의 날짜 범위 출결 데이터 조회 (페이지네이션)
    Page<Attendance> findByMbNoAndWorkDateBetween(Long mbNo, LocalDateTime start, LocalDateTime end, Pageable pageable);

    // 최근 30일 전체 출결 기록 조회
    List<Attendance> findByWorkDateBetween(LocalDateTime start, LocalDateTime end);

    // 회원 정보, 출근 날짜 조회
    Optional<Attendance> findByMbNoAndWorkDate(Long mbNo, LocalDate workDate);

}
