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
    private String type; // "TEXT" 또는 "HTML"

    public EmailRequest(String to, String subject, String content, String type) {
        this.to = to;
        this.subject = subject;
        this.content = content;
        this.type = type;
    }
}
