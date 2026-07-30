package com.vn2bs.common.config.kafka.ThuTuc1;

import java.nio.charset.StandardCharsets;

import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

import com.google.gson.Gson;
import com.vn2bs.common.domains.ThuTuc1.ThuTuc1_GuiHoSo;

import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

/**
 * Supports both Jackson (current outbox) and Gson (legacy outbox) Kafka payloads.
 */
@Slf4j
public class ThuTuc1GuiHoSoKafkaDeserializer implements Deserializer<ThuTuc1_GuiHoSo> {

    private final ObjectMapper objectMapper = JsonMapper.builder().build();
    private final Gson gson = new Gson();

    @Override
    public ThuTuc1_GuiHoSo deserialize(String topic, byte[] data) {
        if (data == null || data.length == 0) {
            return null;
        }

        String json = new String(data, StandardCharsets.UTF_8);

        try {
            return objectMapper.readValue(json, ThuTuc1_GuiHoSo.class);
        } catch (Exception jacksonEx) {
            try {
                ThuTuc1_GuiHoSo legacy = gson.fromJson(json, ThuTuc1_GuiHoSo.class);
                if (legacy != null) {
                    log.warn("Deserialized legacy Gson GuiHoSo payload topic={} maSoHoSo={}",
                            topic, legacy.getMaSoHoSo());
                    return legacy;
                }
            } catch (Exception gsonEx) {
                log.debug("Gson fallback failed topic={}: {}", topic, gsonEx.getMessage());
            }
            throw new SerializationException("Cannot deserialize ThuTuc1_GuiHoSo from topic " + topic, jacksonEx);
        }
    }
}
