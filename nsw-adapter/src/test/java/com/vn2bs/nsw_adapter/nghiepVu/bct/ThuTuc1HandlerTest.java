package com.vn2bs.nsw_adapter.nghiepVu.bct;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.vn2bs.common.domains.BusinessStatus;
import com.vn2bs.common.domains.MessageType;
import com.vn2bs.common.domains.ThuTuc1.ThuTuc1_GuiHoSo;
import com.vn2bs.common.repositories.ThuTuc1.ThuTuc1_GuiHoSoRepository;
import com.vn2bs.common.services.BusinessStatusValidator;
import com.vn2bs.common.services.MessageLogService;
import com.vn2bs.common.services.TraLoiStatusResolver;

@ExtendWith(MockitoExtension.class)
class ThuTuc1HandlerTest {

    @Mock
    private ThuTuc1_GuiHoSoRepository guiHoSoRepository;

    @Mock
    private MessageLogService messageLogService;

    @Mock
    private BusinessStatusValidator businessStatusValidator;

    @InjectMocks
    private ThuTuc1Handler handler;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(handler, "traLoiStatusResolver", new TraLoiStatusResolver());
    }

    @Test
    void traLoi_skipsWhenStatusAlreadyApplied() {
        ThuTuc1_GuiHoSo hoSo = new ThuTuc1_GuiHoSo();
        hoSo.setMaSoHoSo("NSW-2026-0023");
        hoSo.setBusinessStatus(BusinessStatus.DA_PHE_DUYET);

        when(guiHoSoRepository.findByMaSoHoSo("NSW-2026-0023")).thenReturn(Optional.of(hoSo));

        handler.traLoi("""
                {"maSoHoSo":"NSW-2026-0023","ketQua":"okok","correlationId":"corr-1"}
                """);

        verify(businessStatusValidator, never()).validateTransition(any(), any());
        verify(messageLogService, never()).logProcessedSuccess(any(), any(), any(), any(), any());
        assertEquals(BusinessStatus.DA_PHE_DUYET, hoSo.getBusinessStatus());
    }

    @Test
    void traLoi_updatesStatusWhenPending() {
        ThuTuc1_GuiHoSo hoSo = new ThuTuc1_GuiHoSo();
        hoSo.setMaSoHoSo("NSW-2026-0023");
        hoSo.setBusinessStatus(BusinessStatus.CHO_PHE_DUYET);

        when(guiHoSoRepository.findByMaSoHoSo("NSW-2026-0023")).thenReturn(Optional.of(hoSo));

        handler.traLoi("""
                {"maSoHoSo":"NSW-2026-0023","ketQua":"okok","correlationId":"corr-2"}
                """);

        verify(businessStatusValidator).validateTransition(BusinessStatus.CHO_PHE_DUYET, BusinessStatus.DA_PHE_DUYET);
        verify(messageLogService).logProcessedSuccess(
                eq("corr-2"), any(), any(), eq(MessageType.TRA_LOI), eq("NSW-2026-0023"));
        assertEquals(BusinessStatus.DA_PHE_DUYET, hoSo.getBusinessStatus());
    }
}
