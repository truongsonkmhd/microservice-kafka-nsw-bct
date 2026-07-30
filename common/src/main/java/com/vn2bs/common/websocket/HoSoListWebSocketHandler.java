package com.vn2bs.common.websocket;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class HoSoListWebSocketHandler extends TextWebSocketHandler {

    private final HoSoWebSocketBroadcaster broadcaster;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String status = HoSoMaSoWebSocketHandler.extractPathSegment(session.getUri(), 1);
        if (status == null || status.isBlank()) {
            closeQuietly(session, CloseStatus.BAD_DATA);
            return;
        }
        broadcaster.subscribeListStatus(status, session);
        log.info("WS list connected status={} session={}", status, session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        broadcaster.unsubscribe(session);
    }

    private void closeQuietly(WebSocketSession session, CloseStatus status) {
        try {
            session.close(status);
        } catch (Exception ignored) {
            // ignore
        }
    }
}
