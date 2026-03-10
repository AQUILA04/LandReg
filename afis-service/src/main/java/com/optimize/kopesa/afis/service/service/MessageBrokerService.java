package com.optimize.kopesa.afis.service.service;

import com.optimize.kopesa.afis.service.service.dto.MatcherResponseDTO;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class MessageBrokerService {
    private final KafkaTemplate<String, MatcherResponseDTO> kafkaTemplate;

    public MessageBrokerService(KafkaTemplate<String, MatcherResponseDTO> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendResult(MatcherResponseDTO result) {
        kafkaTemplate.send("afis-matcher-result-topic", result);
        System.out.println("Message " + result.toString() +
            " has been sucessfully sent to the topic:  afis-matcher-topic");
    }
}
