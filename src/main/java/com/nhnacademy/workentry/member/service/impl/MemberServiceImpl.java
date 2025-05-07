package com.nhnacademy.workentry.member.service.impl;

import com.nhnacademy.workentry.adaptor.GatewayAdaptor;
import com.nhnacademy.workentry.member.dto.MemberInfoResponse;
import com.nhnacademy.workentry.member.dto.MemberResponse;
import com.nhnacademy.workentry.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * MemberService 인터페이스 구현체입니다.
 * 회원 등록, 조회, 수정, 삭제, 비밀번호 변경 기능을 제공합니다.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final GatewayAdaptor gatewayAdaptor;

    /**
     * 이메일로 회원 정보를 조회합니다.
     *
     * @param mbEmail 조회할 회원 이메일
     * @return 회원 정보 또는 존재하지 않을 경우 null 반환
     */
    @Override
    public MemberResponse getMbEmail(String mbEmail) {
        try {
            ResponseEntity<MemberResponse> member = gatewayAdaptor.getMemberByMbEmail(mbEmail);
            return member.getBody();
        } catch (Exception e) { // todo: Exception 수정
            return null;
        }
    }

    /**
     * 전체 회원 요약 정보를 조회합니다.
     * <p>
     * GatewayAdaptor를 통해 member-service에 요청을 전달하고,
     * 회원 목록을 받아오는 역할을 합니다.
     * 예외 발생 시 로그를 출력하고 빈 리스트를 반환합니다.
     * </p>
     *
     * @return 회원 요약 정보 리스트 (요청 실패 시 빈 리스트 반환)
     */
    @Override
    public List<MemberInfoResponse> getMemberInfoList() {

        try {
            ResponseEntity<List<MemberInfoResponse>> response = gatewayAdaptor.getMemberInfoList();
            return response.getBody();
        } catch (Exception e) {
            log.error("회원 리스트 조회 실패 : {}", e.getMessage());
        }
        return List.of();
    }
}
