package com.vn2bs.nsw_adapter.nghiepVu.bct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.vn2bs.common.config.GlobalConfig;
import com.vn2bs.common.domains.BusinessStatus;
import com.vn2bs.common.domains.MessageParty;
import com.vn2bs.common.domains.MessageType;
import com.vn2bs.common.domains.Status;
import com.vn2bs.common.domains.ThuTuc1.ThuTuc1_GuiHoSo;
import com.vn2bs.common.dto.ThuTuc1.TraLoiDto;
import com.vn2bs.common.repositories.ThuTuc1.ThuTuc1_GuiHoSoRepository;
import com.vn2bs.common.services.BusinessStatusValidator;
import com.vn2bs.common.services.MessageLogService;
import com.vn2bs.common.services.TraLoiStatusResolver;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ThuTuc1Handler {

    private final Gson gson = new Gson();

    @Autowired
    private ThuTuc1_GuiHoSoRepository guiHoSoRepository;

    @Autowired
    private MessageLogService messageLogService;

    @Autowired
    private BusinessStatusValidator businessStatusValidator;

    @Autowired
    private TraLoiStatusResolver traLoiStatusResolver;

    @KafkaListener(topics = GlobalConfig.Kafka.Topic.BCT.ThuTuc1.TRA_LOI_WS, groupId = "bct", containerFactory = "kafkaListenerContainerFactory")
    public void traLoi(String request) {
        TraLoiDto dto = gson.fromJson(request, TraLoiDto.class);
        log.info("Received TraLoi at NSW adapter maSoHoSo={} correlationId={}",
                dto.getMaSoHoSo(), dto.getCorrelationId());

        ThuTuc1_GuiHoSo hoSo = guiHoSoRepository.findByMaSoHoSo(dto.getMaSoHoSo()).orElse(null);
        if (hoSo == null) {
            log.warn("GuiHoSo not found for TraLoi maSoHoSo={}", dto.getMaSoHoSo());
            messageLogService.logProcessedFailed(
                    dto.getCorrelationId(),
                    MessageParty.BCT,
                    MessageParty.NSW,
                    MessageType.TRA_LOI,
                    dto.getMaSoHoSo(),
                    "Ho so not found");
            return;
        }

        BusinessStatus targetStatus = traLoiStatusResolver.resolve(dto.getKetQua());
        if (hoSo.getBusinessStatus() == targetStatus) {
            log.info("TraLoi already applied maSoHoSo={} businessStatus={}",
                    hoSo.getMaSoHoSo(), hoSo.getBusinessStatus());
            return;
        }

        businessStatusValidator.validateTransition(hoSo.getBusinessStatus(), targetStatus);
        hoSo.setBusinessStatus(targetStatus);
        hoSo.setStatus(Status.COMPLETED);
        guiHoSoRepository.save(hoSo);

        messageLogService.logProcessedSuccess(
                dto.getCorrelationId(),
                MessageParty.BCT,
                MessageParty.NSW,
                MessageType.TRA_LOI,
                dto.getMaSoHoSo());

        log.info("Updated GuiHoSo maSoHoSo={} businessStatus={}", hoSo.getMaSoHoSo(), hoSo.getBusinessStatus());
    }
}
