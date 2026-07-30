package com.vn2bs.common.config.kafka.ThuTuc1;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.sql.Timestamp;

import org.junit.jupiter.api.Test;

import com.vn2bs.common.domains.BusinessStatus;
import com.vn2bs.common.domains.ThuTuc1.ThuTuc1_GuiHoSo;

import tools.jackson.databind.json.JsonMapper;

class ThuTuc1GuiHoSoKafkaDeserializerTest {

    private final ThuTuc1GuiHoSoKafkaDeserializer deserializer = new ThuTuc1GuiHoSoKafkaDeserializer();

    @Test
    void deserialize_jacksonIsoDates() throws Exception {
        ThuTuc1_GuiHoSo entity = new ThuTuc1_GuiHoSo();
        entity.setMaSoHoSo("NSW-2026-0099");
        entity.setTenNguoiGui("Test");
        entity.setBusinessStatus(BusinessStatus.KHOI_TAO);
        entity.setCreatedDate(new Timestamp(System.currentTimeMillis()));

        byte[] payload = JsonMapper.builder().build().writeValueAsBytes(entity);

        ThuTuc1_GuiHoSo result = deserializer.deserialize("nsw-thutuc1-guihoso", payload);

        assertNotNull(result);
        assertEquals("NSW-2026-0099", result.getMaSoHoSo());
    }

    @Test
    void deserialize_legacyGsonDates() {
        String legacyJson = """
                {
                  "id": 33,
                  "maSoHoSo": "NSW-2026-0031",
                  "tenNguoiGui": "demo",
                  "businessStatus": "KHOI_TAO",
                  "correlationId": "b85793be-3465-428b-b4c3-00f972c2e1c6",
                  "status": "CREATED",
                  "createdDate": "Jun 16, 2026, 10:35:10 PM",
                  "lastModifiedDate": "Jun 16, 2026, 10:35:10 PM"
                }
                """;

        ThuTuc1_GuiHoSo result = deserializer.deserialize(
                "nsw-thutuc1-guihoso",
                legacyJson.getBytes(java.nio.charset.StandardCharsets.UTF_8));

        assertNotNull(result);
        assertEquals("NSW-2026-0031", result.getMaSoHoSo());
        assertEquals("demo", result.getTenNguoiGui());
    }

    @Test
    void deserialize_legacyGsonDatesWithQuestionMarkTimezone() {
        String legacyJson = """
                {
                  "id": 33,
                  "maSoHoSo": "NSW-2026-0031",
                  "tenNguoiGui": "demo",
                  "businessStatus": "KHOI_TAO",
                  "correlationId": "b85793be-3465-428b-b4c3-00f972c2e1c6",
                  "status": "CREATED",
                  "createdDate": "Jun 16, 2026, 10:35:10?PM",
                  "lastModifiedDate": "Jun 16, 2026, 10:35:10?PM"
                }
                """;

        ThuTuc1_GuiHoSo result = deserializer.deserialize(
                "nsw-thutuc1-guihoso",
                legacyJson.getBytes(java.nio.charset.StandardCharsets.UTF_8));

        assertNotNull(result);
        assertEquals("NSW-2026-0031", result.getMaSoHoSo());
    }
}
