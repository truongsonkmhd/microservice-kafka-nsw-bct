package com.vn2bs.common.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vn2bs.common.domains.OutboxEvent;
import com.vn2bs.common.domains.OutboxStatus;
import com.vn2bs.common.repositories.OutboxEventRepository;

import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

@Service
@Slf4j
public class OutboxService {

    @Autowired
    private OutboxEventRepository outboxEventRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Transactional
    public OutboxEvent enqueue(String topic, String dlqTopic, String payload, String payloadType, String aggregateKey) {
        OutboxEvent event = new OutboxEvent();
        event.setTopic(topic);
        event.setDlqTopic(dlqTopic);
        event.setPayload(payload);
        event.setPayloadType(payloadType);
        event.setAggregateKey(aggregateKey);
        event.setStatus(OutboxStatus.PENDING);
        OutboxEvent saved = outboxEventRepository.save(event);
        log.debug("Outbox enqueued id={} topic={} aggregateKey={}", saved.getId(), topic, aggregateKey);
        return saved;
    }

    @Transactional
    public OutboxEvent enqueueObject(String topic, String dlqTopic, Object payload, String payloadType,
            String aggregateKey) {
        try {
            return enqueue(topic, dlqTopic, objectMapper.writeValueAsString(payload), payloadType, aggregateKey);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to serialize outbox payload for " + aggregateKey, ex);
        }
    }
}
