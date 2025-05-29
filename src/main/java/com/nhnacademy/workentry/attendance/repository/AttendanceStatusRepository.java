package com.nhnacademy.workentry.attendance.repository;

import com.nhnacademy.workentry.attendance.entity.AttendanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 출결 상태(AttendanceStatus) 엔티티에 대한 JPA Repository 인터페이스입니다.
 * <p>
 * 출결 상태 코드 및 설명 데이터를 DB에서 CRUD 할 수 있도록 지원합니다.
 * </p>
 *
 * @see AttendanceStatus
 */
public interface AttendanceStatusRepository extends JpaRepository<AttendanceStatus, Long> {
    Optional<AttendanceStatus> findByDescription(String statusDescription);
}
