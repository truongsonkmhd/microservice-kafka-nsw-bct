package com.vn2bs.bct_adapter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.vn2bs.common.websocket.HoSoListWebSocketHandler;
import com.vn2bs.common.websocket.HoSoWebSocketBroadcaster;

@Configuration
@EnableWebSocket
public class BctHoSoWebSocketConfiguration implements WebSocketConfigurer {

    private final HoSoWebSocketBroadcaster broadcaster;

    public BctHoSoWebSocketConfiguration(HoSoWebSocketBroadcaster broadcaster) {
        this.broadcaster = broadcaster;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(bctHoSoListWebSocketHandler(), "/ws/bct/ho-so/{status}")
                .setAllowedOriginPatterns("*");
    }

    @Bean
    HoSoListWebSocketHandler bctHoSoListWebSocketHandler() {
        return new HoSoListWebSocketHandler(broadcaster);
    }
}
