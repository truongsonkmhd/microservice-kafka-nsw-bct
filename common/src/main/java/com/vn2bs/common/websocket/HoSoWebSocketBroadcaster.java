package com.vn2bs.common.websocket;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.vn2bs.common.dto.HoSoStatusEvent;

import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

@Component
@Slf4j
public class HoSoWebSocketBroadcaster {

    private final ObjectMapper objectMapper;
    private final Map<String, Set<WebSocketSession>> byMaSoHoSo = new ConcurrentHashMap<>();
    private final Map<String, Set<WebSocketSession>> byListStatus = new ConcurrentHashMap<>();
    private final Map<String, BusinessStatusSnapshot> lastKnownStatus = new ConcurrentHashMap<>();

    public HoSoWebSocketBroadcaster(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void subscribeMaSoHoSo(String maSoHoSo, WebSocketSession session) {
        byMaSoHoSo.computeIfAbsent(maSoHoSo, key -> new CopyOnWriteArraySet<>()).add(session);
        log.debug("WS subscribe maSoHoSo={} session={}", maSoHoSo, session.getId());
    }

    public void subscribeListStatus(String status, WebSocketSession session) {
        byListStatus.computeIfAbsent(status, key -> new CopyOnWriteArraySet<>()).add(session);
        log.debug("WS subscribe list status={} session={}", status, session.getId());
    }

    public void unsubscribe(WebSocketSession session) {
        byMaSoHoSo.values().forEach(set -> set.remove(session));
        byListStatus.values().forEach(set -> set.remove(session));
    }

    public Set<String> activeMaSoHoSoSubscriptions() {
        return byMaSoHoSo.entrySet().stream()
                .filter(entry -> !entry.getValue().isEmpty())
                .map(Map.Entry::getKey)
                .collect(java.util.stream.Collectors.toSet());
    }

    public boolean publishIfChanged(HoSoStatusEvent event) {
        if (event.getMaSoHoSo() == null || event.getBusinessStatus() == null) {
            return false;
        }
        BusinessStatusSnapshot previous = lastKnownStatus.get(event.getMaSoHoSo());
        BusinessStatusSnapshot current = new BusinessStatusSnapshot(
                event.getBusinessStatus(),
                event.getKetQua());
        if (previous != null && previous.equals(current)) {
            return false;
        }
        lastKnownStatus.put(event.getMaSoHoSo(), current);
        broadcastMaSoHoSo(event);
        return true;
    }

    public void publishMaSoHoSo(HoSoStatusEvent event) {
        if (event.getMaSoHoSo() != null && event.getBusinessStatus() != null) {
            lastKnownStatus.put(
                    event.getMaSoHoSo(),
                    new BusinessStatusSnapshot(event.getBusinessStatus(), event.getKetQua()));
        }
        broadcastMaSoHoSo(event);
    }

    public void publishList(HoSoStatusEvent event) {
        if (event.getBusinessStatus() == null) {
            return;
        }
        Set<WebSocketSession> sessions = byListStatus.get(event.getBusinessStatus().name());
        if (sessions == null || sessions.isEmpty()) {
            return;
        }
        sendToSessions(sessions, event);
    }

    private void broadcastMaSoHoSo(HoSoStatusEvent event) {
        if (event.getMaSoHoSo() == null) {
            return;
        }
        Set<WebSocketSession> sessions = byMaSoHoSo.get(event.getMaSoHoSo());
        if (sessions == null || sessions.isEmpty()) {
            return;
        }
        sendToSessions(sessions, event);
    }

    private void sendToSessions(Set<WebSocketSession> sessions, HoSoStatusEvent event) {
        String payload;
        try {
            payload = objectMapper.writeValueAsString(event);
        } catch (Exception ex) {
            log.error("Failed to serialize HoSoStatusEvent", ex);
            return;
        }
        TextMessage message = new TextMessage(payload);
        for (WebSocketSession session : sessions) {
            if (!session.isOpen()) {
                sessions.remove(session);
                continue;
            }
            try {
                session.sendMessage(message);
            } catch (IOException ex) {
                log.warn("WS send failed session={}: {}", session.getId(), ex.getMessage());
                sessions.remove(session);
            }
        }
    }

    private record BusinessStatusSnapshot(
            com.vn2bs.common.domains.BusinessStatus businessStatus,
            String ketQua) {
    }
}
