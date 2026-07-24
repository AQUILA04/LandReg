package com.optimize.kopesa.afis.service.service;

import com.optimize.common.blob.kafka.FingerWorkerResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.optimize.kopesa.afis.service.service.dto.MatcherResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class MessageBrokerService {
    private final Logger log = LoggerFactory.getLogger(MessageBrokerService.class);
    public static final String TOPIC_FINGER_RES = "biometrics.finger.res";
    public static final String TOPIC_FINGER_DLQ = "biometrics.finger.dlq";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public MessageBrokerService(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendResult(MatcherResponseDTO result) throws JsonProcessingException {
        kafkaTemplate.send("afis-matcher-result-topic", objectMapper.writeValueAsString(result));
        log.info("Legacy matcher result sent for rid {}", result.getRid());
    }

    public void sendFingerResult(FingerWorkerResponse result) throws JsonProcessingException {
        kafkaTemplate.send(TOPIC_FINGER_RES, objectMapper.writeValueAsString(result));
        log.info("Finger result sent rid={} finger={} status={}", result.getRid(), result.getFingerId(), result.getStatus());
    }

    public void sendFingerDlq(String payload) {
        kafkaTemplate.send(TOPIC_FINGER_DLQ, payload);
        log.warn("Finger request routed to DLQ");
    }
}
