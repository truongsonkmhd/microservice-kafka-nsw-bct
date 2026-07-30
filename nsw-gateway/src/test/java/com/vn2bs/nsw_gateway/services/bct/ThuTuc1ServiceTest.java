package com.vn2bs.nsw_gateway.services.bct;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.vn2bs.common.domains.BusinessStatus;
import com.vn2bs.common.domains.ThuTuc1.ThuTuc1_GuiHoSo;
import com.vn2bs.common.domains.ThuTuc1.ThuTuc1_TraLoi;
import com.vn2bs.common.repositories.ThuTuc1.ThuTuc1_GuiHoSoRepository;
import com.vn2bs.common.repositories.ThuTuc1.ThuTuc1_TraLoiRepository;
import com.vn2bs.common.services.BusinessStatusValidator;
import com.vn2bs.common.services.MessageLogService;
import com.vn2bs.common.services.OutboxService;
import com.vn2bs.common.services.TraLoiStatusResolver;
import com.vn2bs.nsw_gateway.xsd.bct.thutuc1.TraLoiRequest;

@ExtendWith(MockitoExtension.class)
class ThuTuc1ServiceTest {

    @Mock
    private ThuTuc1_TraLoiRepository traLoiRepository;

    @Mock
    private ThuTuc1_GuiHoSoRepository guiHoSoRepository;

    @Mock
    private MessageLogService messageLogService;

    @Mock
    private OutboxService outboxService;

    @Mock
    private BusinessStatusValidator businessStatusValidator;

    @InjectMocks
    private ThuTuc1Service thuTuc1Service;

    private final TraLoiStatusResolver traLoiStatusResolver = new TraLoiStatusResolver();

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        org.springframework.test.util.ReflectionTestUtils.setField(
                thuTuc1Service, "traLoiStatusResolver", traLoiStatusResolver);
    }

    @Test
    void traLoi_upsertsTraLoiAndUpdatesBusinessStatus() {
        ThuTuc1_GuiHoSo hoSo = new ThuTuc1_GuiHoSo();
        hoSo.setMaSoHoSo("NSW-2026-0023");
        hoSo.setBusinessStatus(BusinessStatus.CHO_PHE_DUYET);

        when(guiHoSoRepository.findByMaSoHoSo("NSW-2026-0023")).thenReturn(Optional.of(hoSo));
        when(traLoiRepository.findFirstByMaSoHoSoOrderByLastModifiedDateDesc("NSW-2026-0023"))
                .thenReturn(Optional.empty());
        when(messageLogService.isAlreadyProcessed(any())).thenReturn(false);

        TraLoiRequest request = new TraLoiRequest();
        request.setMaSoHoSo("NSW-2026-0023");
        request.setKetQua("okok");

        thuTuc1Service.traLoi(request);

        ArgumentCaptor<ThuTuc1_TraLoi> traLoiCaptor = ArgumentCaptor.forClass(ThuTuc1_TraLoi.class);
        verify(traLoiRepository).save(traLoiCaptor.capture());
        assertEquals("okok", traLoiCaptor.getValue().getKetQua());

        verify(businessStatusValidator).validateTransition(BusinessStatus.CHO_PHE_DUYET, BusinessStatus.DA_PHE_DUYET);
        verify(guiHoSoRepository).save(hoSo);
        assertEquals(BusinessStatus.DA_PHE_DUYET, hoSo.getBusinessStatus());
        verify(outboxService).enqueue(any(), any(), any(), eq("TraLoiDto"), eq("NSW-2026-0023"));
    }
}
