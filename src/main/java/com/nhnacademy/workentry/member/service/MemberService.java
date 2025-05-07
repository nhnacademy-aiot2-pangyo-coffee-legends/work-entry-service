package com.nhnacademy.workentry.member.service;


import com.nhnacademy.workentry.member.dto.MemberInfoResponse;
import com.nhnacademy.workentry.member.dto.MemberResponse;

import java.util.List;

public interface MemberService {
    MemberResponse getMbEmail(String mbEmail);

    List<MemberInfoResponse> getMemberInfoList();
}
