package com.nhnacademy.workentry.adaptor;

import com.nhnacademy.workentry.adaptor.client.MemberServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class MemberAdaptor {

    private final MemberServiceClient memberServiceClient;

    public List<Long> getAllMemberIds() {
        List<Long> memberIds = memberServiceClient.getAllMemberIds();
        log.info("📋 불러온 회원 mbNo 리스트: {}", memberIds);
        return memberIds;
    }
}