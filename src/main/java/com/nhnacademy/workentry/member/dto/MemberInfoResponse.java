package com.nhnacademy.workentry.member.dto;

import lombok.Value;

/**
 * 회원 기본 정보 응답 DTO입니다.
 * <p>
 * 이 클래스는 FeignClient를 통해 다른 마이크로서비스(예: work-entry-service)에서
 * 회원 번호, 이름, 이메일, 전화번호와 같은 최소한의 사용자 정보를 받아오기 위한 용도로 사용됩니다.
 * 특히 근무시간 통계, 출결 현황 등의 기능에서 회원의 이름이나 연락처 정보를 표시할 필요가 있을 때 사용됩니다.
 * </p>
 * <p>
 * 이 DTO는 불변(immutable) 객체로 설계되어 있어, 응답 데이터의 안정성과 신뢰성을 높이는 데 기여합니다.
 * </p>
 */
@Value
public class MemberInfoResponse {

    Long no;
    String name;
    String email;
    String phoneNumber;
}
