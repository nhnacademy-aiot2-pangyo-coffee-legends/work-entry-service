package com.nhnacademy.workentry.adapter.notify.controller;

import com.nhnacademy.workentry.adapter.notify.dto.EmailSender;
import com.nhnacademy.workentry.entry.email.dto.EmailRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/email")
public class EmailController {
    private final EmailSender emailSender;

    public EmailController(EmailSender emailSender){
        this.emailSender = emailSender;
    }

    @PostMapping("/text")
    public void sendEmail(@Validated @RequestBody EmailRequest request){
        emailSender.sendEmail(request);
    }
}
