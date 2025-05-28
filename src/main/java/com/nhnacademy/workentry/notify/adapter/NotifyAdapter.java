package com.nhnacademy.workentry.notify.adapter;

import com.nhnacademy.workentry.notify.dto.EmailRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "notify-service", url = "${notify.service.url}")
public interface NotifyAdapter {

    //text로 보내느냐 html로 보내느냐 그것이 고민이로다.
    @PostMapping("api/v1/email/text")
    ResponseEntity<String> sendTextEmail(@Validated @RequestBody EmailRequest request);

    @PostMapping("api/v1/email/html")
    ResponseEntity<String> sendHtmlEmail(@Validated @RequestBody EmailRequest request);
}
