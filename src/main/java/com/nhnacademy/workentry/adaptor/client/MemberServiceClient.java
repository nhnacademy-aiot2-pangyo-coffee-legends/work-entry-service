package com.nhnacademy.workentry.adaptor.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "member-service", url = "${member.service.url}")
public interface MemberServiceClient {
    
    @GetMapping("/api/v1/members/ids")
    List<Long> getAllMemberIds();
}