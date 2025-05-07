package com.nhnacademy.workentry.adaptor;


import com.nhnacademy.workentry.authenfication.token.JwtIssueRequest;
import com.nhnacademy.workentry.authenfication.token.JwtResponse;
import com.nhnacademy.workentry.authenfication.token.TokenRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Gateway를 통해 member-service 및 attendance-service와 통신하는 어댑터입니다.
 */
@FeignClient(name = "gateway-service", url = "http://localhost:10251", path = "/api/v1")
public interface GatewayAdaptor {

    /**
     * 이메일을 기준으로 회원 정보를 조회합니다.
     *
     * @param email 조회할 회원 이메일
     * @return 조회된 회원 정보
     */
//    @GetMapping("/members/email/{mbEmail}")
//    ResponseEntity<MemberResponse> getMemberByMbEmail(@PathVariable("mbEmail") String email);

    /**
     * JWT 토큰 발급 요청을 보냅니다.
     *
     * @param jwtIssueRequest JWT 발급 요청 데이터
     * @return 발급된 JWT 토큰 정보
     */
    @PostMapping("/token")
    ResponseEntity<JwtResponse> issueToken(@RequestBody JwtIssueRequest jwtIssueRequest);

    /**
     * JWT 토큰 재발급 요청을 보냅니다.
     *
     * @param request JWT 요청 데이터
     * @return 재발급된 JWT 토큰 정보
     */
    @PostMapping("/token/reissue")
    ResponseEntity<JwtResponse> reissueToken(@RequestBody TokenRequest request);

//    /**
//     * 특정 회원의 지정된 기간 동안 출결 데이터를 조회합니다.
//     *
//     * @param no 회원 번호
//     * @param start 조회 시작 시간
//     * @param end 조회 종료 시간
//     * @return 조회된 출결 목록
//     */
//    @GetMapping("/attendances/{no}")
//    ResponseEntity<List<AttendanceDto>> getAttendanceByNoAndDateRange(
//            @PathVariable("no") Long no,
//            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
//            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end
//    );
//
//    /**
//     * 최근 7일간의 출결 요약 데이터를 조회합니다.
//     *
//     * @return 최근 출결 요약 목록
//     */
//    @GetMapping("/attendances/summary/recent")
//    ResponseEntity<List<AttendanceDto>> getRecentAttendanceSummary();
//
//    /**
//     * 지정된 회원 번호에 해당하는 최근 30일간의 근무 요약 정보를 조회합니다.
//     *
//     * @param no 회원 번호 (mb_no)
//     * @return 해당 회원의 근무일자별 출근·퇴근·근무시간·상태 정보를 담은 리스트
//     */
//    @GetMapping("/attendances/summary/recent/{no}")
//    ResponseEntity<List<AttendanceSummaryDto>>getRecentWorkingHoursByMember(@PathVariable Long no);
}
