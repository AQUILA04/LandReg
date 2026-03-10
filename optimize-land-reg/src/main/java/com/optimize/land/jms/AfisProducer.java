package com.optimize.land.jms;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final FingerprintMatchingHistoryService fingerprintMatchingHistoryService;

    public void sendMatchingRequest(AfisMasterRequest afisMasterRequest) throws JsonProcessingException {
        log.info("SENDING MATCHING REQUEST: {} | size: {}", afisMasterRequest.getRid(), afisMasterRequest.getFingerprintStores().size());
        fingerprintMatchingHistoryService.create(new FingerprintMatchingHistory(afisMasterRequest.getRid()));
        kafkaTemplate.send("afis-master-topic", new ObjectMapper().writeValueAsString(afisMasterRequest) );
        log.info("MATCHING REQUEST SENT: {} | size: {}", afisMasterRequest.getRid(), afisMasterRequest.getFingerprintStores().size());
    }
}
