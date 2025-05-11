package com.nhnacademy.workentry.attendance.repository;

import com.nhnacademy.workentry.attendance.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 출결 정보를 관리하는 JPA Repository입니다.
 */
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    // 회원 번호로 전체 출결 기록 조회
    List<Attendance> findAllByMbNo(Long mbNo);

    // 회원 번호와 날짜 범위로 출결 기록 조회
    List<Attendance> findByMbNoAndWorkDateBetween(Long mbNo, LocalDateTime start, LocalDateTime end);

    // 최근 30일 전체 출결 기록 조회
    List<Attendance> findByWorkDateBetween(LocalDateTime start, LocalDateTime end);

    // 회원 정보, 출근 날짜 조회
    Optional<Attendance> findByMbNoAndWorkDate(Long mbNo, LocalDateTime workDate);
}
