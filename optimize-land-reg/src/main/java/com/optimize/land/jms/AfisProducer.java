package com.optimize.land.jms;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.optimize.common.blob.kafka.AfisMasterRequestV2;
import com.optimize.land.jms.model.AfisMasterRequest;
import com.optimize.land.model.entity.FingerprintMatchingHistory;
import com.optimize.land.service.FingerprintMatchingHistoryService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class AfisProducer {

    public static final String TOPIC_LEGACY = "afis-master-topic";
    public static final String TOPIC_MATCH_REQ = "biometrics.match.req";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final FingerprintMatchingHistoryService fingerprintMatchingHistoryService;
    private final ObjectMapper objectMapper;

    public void sendMatchingRequest(AfisMasterRequest afisMasterRequest) throws JsonProcessingException {
        log.info("SENDING MATCHING REQUEST v1: {} | size: {}", afisMasterRequest.getRid(), afisMasterRequest.getFingerprintStores().size());
        fingerprintMatchingHistoryService.create(new FingerprintMatchingHistory(afisMasterRequest.getRid()));
        kafkaTemplate.send(TOPIC_LEGACY, objectMapper.writeValueAsString(afisMasterRequest));
        log.info("MATCHING REQUEST SENT v1: {}", afisMasterRequest.getRid());
    }

    public void sendMatchingRequestV2(AfisMasterRequestV2 request) throws JsonProcessingException {
        String payload = objectMapper.writeValueAsString(request);
        log.info("SENDING MATCHING REQUEST v2: {} | fingers: {} | bytes: {}", request.getRid(), request.getFingers().size(), payload.length());
        fingerprintMatchingHistoryService.create(new FingerprintMatchingHistory(request.getRid()));
        kafkaTemplate.send(TOPIC_MATCH_REQ, payload);
        kafkaTemplate.send(TOPIC_LEGACY, payload);
        log.info("MATCHING REQUEST SENT v2: {}", request.getRid());
    }
}
