package com.nhnacademy.workentry.adapter.member.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberNoResponse {

    @JsonProperty("memberNo")
    Long mbNo;
}
