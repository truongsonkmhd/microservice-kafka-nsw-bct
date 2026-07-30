package com.vn2bs.nsw_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.vn2bs.common.dto.HoSoStatusEvent;
import com.vn2bs.common.repositories.ThuTuc1.ThuTuc1_GuiHoSoRepository;
import com.vn2bs.common.repositories.ThuTuc1.ThuTuc1_TraLoiRepository;
import com.vn2bs.common.websocket.HoSoMaSoWebSocketHandler;
import com.vn2bs.common.websocket.HoSoWebSocketBroadcaster;

@Configuration
@EnableWebSocket
public class NswHoSoWebSocketConfiguration implements WebSocketConfigurer {

    private final HoSoWebSocketBroadcaster broadcaster;
    private final ThuTuc1_GuiHoSoRepository guiHoSoRepository;
    private final ThuTuc1_TraLoiRepository traLoiRepository;

    public NswHoSoWebSocketConfiguration(
            HoSoWebSocketBroadcaster broadcaster,
            ThuTuc1_GuiHoSoRepository guiHoSoRepository,
            ThuTuc1_TraLoiRepository traLoiRepository) {
        this.broadcaster = broadcaster;
        this.guiHoSoRepository = guiHoSoRepository;
        this.traLoiRepository = traLoiRepository;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(nswHoSoWebSocketHandler(), "/ws/nsw/ho-so/{maSoHoSo}")
                .setAllowedOriginPatterns("*");
    }

    @Bean
    HoSoMaSoWebSocketHandler nswHoSoWebSocketHandler() {
        return new HoSoMaSoWebSocketHandler(broadcaster, this::snapshot);
    }

    private HoSoStatusEvent snapshot(String maSoHoSo) {
        return guiHoSoRepository.findByMaSoHoSo(maSoHoSo)
                .map(hoSo -> {
                    String ketQua = traLoiRepository.findFirstByMaSoHoSoOrderByLastModifiedDateDesc(maSoHoSo)
                            .map(com.vn2bs.common.domains.ThuTuc1.ThuTuc1_TraLoi::getKetQua)
                            .orElse(null);
                    return HoSoStatusEvent.snapshot(
                            hoSo.getMaSoHoSo(),
                            hoSo.getTenNguoiGui(),
                            hoSo.getBusinessStatus(),
                            ketQua);
                })
                .orElse(null);
    }
}
