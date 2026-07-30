package com.vn2bs.common.websocket;

import java.net.URI;
import java.util.function.Function;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.vn2bs.common.dto.HoSoStatusEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class HoSoMaSoWebSocketHandler extends TextWebSocketHandler {

    private final HoSoWebSocketBroadcaster broadcaster;
    private final Function<String, HoSoStatusEvent> snapshotProvider;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String maSoHoSo = extractPathSegment(session.getUri(), 1);
        if (maSoHoSo == null || maSoHoSo.isBlank()) {
            closeQuietly(session, CloseStatus.BAD_DATA);
            return;
        }
        broadcaster.subscribeMaSoHoSo(maSoHoSo, session);
        HoSoStatusEvent snapshot = snapshotProvider.apply(maSoHoSo);
        if (snapshot != null) {
            broadcaster.publishMaSoHoSo(snapshot);
        }
        log.info("WS connected maSoHoSo={} session={}", maSoHoSo, session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        broadcaster.unsubscribe(session);
    }

    static String extractPathSegment(URI uri, int indexFromEnd) {
        if (uri == null || uri.getPath() == null) {
            return null;
        }
        String[] parts = uri.getPath().split("/");
        int index = parts.length - indexFromEnd;
        if (index < 0 || index >= parts.length) {
            return null;
        }
        String value = parts[index];
        return value.isBlank() ? null : value;
    }

    private void closeQuietly(WebSocketSession session, CloseStatus status) {
        try {
            session.close(status);
        } catch (Exception ignored) {
            // ignore
        }
    }
}
