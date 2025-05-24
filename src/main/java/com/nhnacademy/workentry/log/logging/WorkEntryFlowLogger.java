package com.nhnacademy.workentry.log.logging;

import com.nhnacademy.traceloggermodule.logging.FlowLogger;
import com.nhnacademy.workentry.adapter.member.client.MemberServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WorkEntryFlowLogger extends FlowLogger {
    private final MemberServiceClient memberServiceClient;

     @Async("modelDispatcherExecutor")
    public void logAndNotifyAbnormalEntry(Long no){


    }

}
