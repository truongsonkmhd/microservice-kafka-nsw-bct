package com.vn2bs.common.websocket;

import org.springframework.stereotype.Service;

import com.vn2bs.common.domains.BusinessStatus;
import com.vn2bs.common.domains.ThuTuc1.ThuTuc1_GuiHoSo;
import com.vn2bs.common.dto.HoSoStatusEvent;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HoSoStatusNotifier {

    private final HoSoWebSocketBroadcaster broadcaster;

    public void notifyMaSoHoSoChanged(ThuTuc1_GuiHoSo hoSo, String ketQua) {
        if (hoSo == null || hoSo.getMaSoHoSo() == null) {
            return;
        }
        broadcaster.publishIfChanged(HoSoStatusEvent.statusChanged(
                hoSo.getMaSoHoSo(),
                hoSo.getTenNguoiGui(),
                hoSo.getBusinessStatus(),
                ketQua));
    }

    public void notifyMaSoHoSoCreated(ThuTuc1_GuiHoSo hoSo) {
        if (hoSo == null || hoSo.getMaSoHoSo() == null) {
            return;
        }
        broadcaster.publishMaSoHoSo(HoSoStatusEvent.statusChanged(
                hoSo.getMaSoHoSo(),
                hoSo.getTenNguoiGui(),
                hoSo.getBusinessStatus(),
                null));
    }

    public void notifyListItemAdded(ThuTuc1_GuiHoSo hoSo) {
        if (hoSo == null || hoSo.getBusinessStatus() == null) {
            return;
        }
        broadcaster.publishList(HoSoStatusEvent.listItemAdded(
                hoSo.getMaSoHoSo(),
                hoSo.getTenNguoiGui(),
                hoSo.getBusinessStatus()));
    }

    public void notifyListItemRemoved(String maSoHoSo, BusinessStatus previousStatus) {
        broadcaster.publishList(HoSoStatusEvent.listItemRemoved(maSoHoSo, previousStatus));
    }
}
