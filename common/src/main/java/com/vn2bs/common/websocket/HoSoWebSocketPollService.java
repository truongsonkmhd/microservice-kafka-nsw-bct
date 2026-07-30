package com.vn2bs.common.websocket;

import java.util.Optional;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.vn2bs.common.domains.ThuTuc1.ThuTuc1_GuiHoSo;
import com.vn2bs.common.domains.ThuTuc1.ThuTuc1_TraLoi;
import com.vn2bs.common.repositories.ThuTuc1.ThuTuc1_GuiHoSoRepository;
import com.vn2bs.common.repositories.ThuTuc1.ThuTuc1_TraLoiRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@ConditionalOnProperty(name = "ho-so.ws.poll.enabled", havingValue = "true")
@RequiredArgsConstructor
@Slf4j
public class HoSoWebSocketPollService {

    private final HoSoWebSocketBroadcaster broadcaster;
    private final ThuTuc1_GuiHoSoRepository guiHoSoRepository;
    private final ThuTuc1_TraLoiRepository traLoiRepository;
    private final HoSoStatusNotifier statusNotifier;

    @Scheduled(fixedDelayString = "${ho-so.ws.poll-interval-ms:2000}")
    public void pollActiveSubscriptions() {
        for (String maSoHoSo : broadcaster.activeMaSoHoSoSubscriptions()) {
            Optional<ThuTuc1_GuiHoSo> hoSo = guiHoSoRepository.findByMaSoHoSo(maSoHoSo);
            if (hoSo.isEmpty()) {
                continue;
            }
            String ketQua = traLoiRepository.findFirstByMaSoHoSoOrderByLastModifiedDateDesc(maSoHoSo)
                    .map(ThuTuc1_TraLoi::getKetQua)
                    .orElse(null);
            statusNotifier.notifyMaSoHoSoChanged(hoSo.get(), ketQua);
        }
    }
}
