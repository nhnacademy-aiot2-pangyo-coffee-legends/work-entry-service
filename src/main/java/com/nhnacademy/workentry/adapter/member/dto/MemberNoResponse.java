package com.nhnacademy.workentry.adapter.member.dto;

public record MemberNoResponse(Long no) {
    // 테스트 코드용 생성자
    public static MemberNoResponse of(Long no) {
        return new MemberNoResponse(no);
    }
}
