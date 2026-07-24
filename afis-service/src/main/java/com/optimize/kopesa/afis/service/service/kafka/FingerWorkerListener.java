package com.optimize.kopesa.afis.service.service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.optimize.common.blob.kafka.FingerMatchStatus;
import com.optimize.common.blob.kafka.FingerWorkerRequest;
import com.optimize.common.blob.kafka.FingerWorkerResponse;
import com.optimize.kopesa.afis.service.config.AfisPipelineProperties;
import com.optimize.kopesa.afis.service.service.FingerMatchingPipelineService;
import com.optimize.kopesa.afis.service.service.MessageBrokerService;
import com.optimize.kopesa.afis.service.service.metrics.AfisMetricsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
public class FingerWorkerListener {

    private static final Logger log = LoggerFactory.getLogger(FingerWorkerListener.class);

    public static final String TOPIC_FINGER_REQ = "biometrics.finger.req";

    private final ObjectMapper objectMapper;
    private final FingerMatchingPipelineService pipelineService;
    private final MessageBrokerService messageBrokerService;
    private final AfisPipelineProperties properties;
    private final AfisMetricsService metricsService;

    public FingerWorkerListener(
        ObjectMapper objectMapper,
        FingerMatchingPipelineService pipelineService,
        MessageBrokerService messageBrokerService,
        AfisPipelineProperties properties,
        AfisMetricsService metricsService
    ) {
        this.objectMapper = objectMapper;
        this.pipelineService = pipelineService;
        this.messageBrokerService = messageBrokerService;
        this.properties = properties;
        this.metricsService = metricsService;
    }

    @KafkaListener(topics = TOPIC_FINGER_REQ, groupId = "afis-service-finger", containerFactory = "kafkaListenerContainerFactory")
    public void processFingerRequest(String message, Acknowledgment acknowledgment) throws JsonProcessingException {
        if (!"v2".equalsIgnoreCase(properties.getVersion())) {
            acknowledgment.acknowledge();
            return;
        }
        metricsService.recordKafkaMessageSize(message.length());
        FingerWorkerRequest request = objectMapper.readValue(message, FingerWorkerRequest.class);
        log.info("Processing finger request rid={} finger={}", request.getRid(), request.getFingerId());
        FingerWorkerResponse response = pipelineService.process(request);
        if (FingerMatchStatus.ERROR.equals(response.getStatus())) {
            messageBrokerService.sendFingerDlq(objectMapper.writeValueAsString(response));
        } else {
            messageBrokerService.sendFingerResult(response);
        }
        acknowledgment.acknowledge();
    }
}
