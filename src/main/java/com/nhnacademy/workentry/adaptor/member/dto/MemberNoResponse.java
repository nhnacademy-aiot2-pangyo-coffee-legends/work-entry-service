package com.nhnacademy.workentry.adaptor.member.dto;

import lombok.Value;

@Value
public class MemberNoResponse {
    Long no;

    // 테스트용 생성자
    public static MemberNoResponse of(Long no) {
        return new MemberNoResponse(no);
    }
}
