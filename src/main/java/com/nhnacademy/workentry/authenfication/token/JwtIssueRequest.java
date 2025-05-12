package com.nhnacademy.workentry.authenfication.token;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class JwtIssueRequest {
    private String email;
    private String role;
}
