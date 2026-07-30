package com.vn2bs.common.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;

public final class KafkaPublishHelper {

    private static final Logger log = LoggerFactory.getLogger(KafkaPublishHelper.class);

    private KafkaPublishHelper() {
    }

    public static <T> void sendOrDlq(KafkaTemplate<String, T> kafkaTemplate, String topic, String dlqTopic, T payload) {
        try {
            kafkaTemplate.send(topic, payload);

            log.debug("Kafka publish ok topic={}", topic);
        } catch (Exception ex) {
            log.error("Kafka publish failed topic={} error={}", topic, ex.getMessage());
            try {
                kafkaTemplate.send(dlqTopic, payload);
                log.warn("Published to DLQ topic={}", dlqTopic);
            } catch (Exception dlqEx) {
                log.error("DLQ publish also failed topic={} error={}", dlqTopic, dlqEx.getMessage());
            }
        }
    }

    public static void sendStringOrDlq(KafkaTemplate<String, String> kafkaTemplate, String topic, String dlqTopic,
            String payload) {
        sendOrDlq(kafkaTemplate, topic, dlqTopic, payload);
    }
}
