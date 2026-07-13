package com.optimize.kopesa.afis.master.broker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.optimize.common.blob.kafka.FingerWorkerRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class FingerWorkerProducer {

    public static final String TOPIC_FINGER_REQ = "biometrics.finger.req";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public FingerWorkerProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendFingerRequest(FingerWorkerRequest request) throws JsonProcessingException {
        kafkaTemplate.send(TOPIC_FINGER_REQ, objectMapper.writeValueAsString(request));
    }
}
