package com.nhnacademy.workentry.log.realtime;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

/**
 * {@code WebSocketLogAppender}는 Logback의 커스텀 Appender로,
 * 로그 이벤트가 발생할 때 해당 로그 메시지를 WebSocket을 통해 실시간으로 브라우저 클라이언트에 전송합니다.
 *
 * <p>이 Appender는 {@link LogWebSocketHandler}를 통해 웹소켓 세션에 접근하고,
 * 로그 메시지를 브로드캐스트합니다. 로그 출력 외에 실시간 모니터링이 필요한 상황에서 유용합니다.</p>
 *
 * <p>핵심 구성 요소:</p>
 * <ul>
 *     <li>{@code LogWebSocketHandler}: WebSocket 세션을 관리하고 메시지를 전송하는 핸들러</li>
 *     <li>{@code WebSocketContextHolder}: WebSocket 핸들러를 정적으로 접근하기 위한 Context Holder</li>
 * </ul>
 *
 */
public class WebSocketLogAppender extends AppenderBase<ILoggingEvent> {

    /**
     * 로그 이벤트 발생 시 호출되어, 해당 로그 메시지를 WebSocket을 통해 브라우저로 전송합니다.
     *
     * @param eventObject 발생한 로그 이벤트
     */
    @Override
    protected void append(ILoggingEvent eventObject) {
        LogWebSocketHandler handler = WebSocketContextHolder.getHandler();
        if (handler != null && eventObject != null) {
            String formattedMessage = eventObject.getFormattedMessage();
            handler.broadcast(formattedMessage);
        }
    }
}
