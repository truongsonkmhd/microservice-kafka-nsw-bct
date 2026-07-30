package com.vn2bs.common.dto;

import java.time.Instant;

import com.vn2bs.common.domains.BusinessStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HoSoStatusEvent {

    public enum EventType {
        SNAPSHOT,
        STATUS_CHANGED,
        LIST_ITEM_ADDED,
        LIST_ITEM_REMOVED
    }

    private String maSoHoSo;
    private String tenNguoiGui;
    private BusinessStatus businessStatus;
    private String ketQua;
    private EventType eventType;
    private Instant timestamp;

    public static HoSoStatusEvent snapshot(String maSoHoSo, String tenNguoiGui, BusinessStatus businessStatus,
            String ketQua) {
        return HoSoStatusEvent.builder()
                .maSoHoSo(maSoHoSo)
                .tenNguoiGui(tenNguoiGui)
                .businessStatus(businessStatus)
                .ketQua(ketQua)
                .eventType(EventType.SNAPSHOT)
                .timestamp(Instant.now())
                .build();
    }

    public static HoSoStatusEvent statusChanged(String maSoHoSo, String tenNguoiGui, BusinessStatus businessStatus,
            String ketQua) {
        return HoSoStatusEvent.builder()
                .maSoHoSo(maSoHoSo)
                .tenNguoiGui(tenNguoiGui)
                .businessStatus(businessStatus)
                .ketQua(ketQua)
                .eventType(EventType.STATUS_CHANGED)
                .timestamp(Instant.now())
                .build();
    }

    public static HoSoStatusEvent listItemAdded(String maSoHoSo, String tenNguoiGui, BusinessStatus businessStatus) {
        return HoSoStatusEvent.builder()
                .maSoHoSo(maSoHoSo)
                .tenNguoiGui(tenNguoiGui)
                .businessStatus(businessStatus)
                .eventType(EventType.LIST_ITEM_ADDED)
                .timestamp(Instant.now())
                .build();
    }

    public static HoSoStatusEvent listItemRemoved(String maSoHoSo, BusinessStatus previousStatus) {
        return HoSoStatusEvent.builder()
                .maSoHoSo(maSoHoSo)
                .businessStatus(previousStatus)
                .eventType(EventType.LIST_ITEM_REMOVED)
                .timestamp(Instant.now())
                .build();
    }
}
