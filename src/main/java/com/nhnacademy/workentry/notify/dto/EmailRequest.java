package com.nhnacademy.workentry.notify.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class EmailRequest implements Serializable {

    private String to;
    private String subject;
    private String content;
    private String roleType; // "ROLE_ADMIN" 또는 "ROLE_ALL"
    private String type; // "TEXT" 또는 "HTML"

    public EmailRequest(String to, String subject, String content, String roleType) {
        this.to = to;
        this.subject = subject;
        this.content = content;
        this.roleType = roleType;
        this.type = null;
    }
}