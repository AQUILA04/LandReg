package com.optimize.kopesa.afis.service.service;

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
    private final KafkaTemplate<String, String> kafkaTemplate;

    public MessageBrokerService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendResult(MatcherResponseDTO result) throws JsonProcessingException {
        kafkaTemplate.send("afis-matcher-result-topic", new ObjectMapper().writeValueAsString(result));
        log.info("Message " + result.toString() +
            " has been sucessfully sent to the topic:  afis-matcher-topic");
    }
}
