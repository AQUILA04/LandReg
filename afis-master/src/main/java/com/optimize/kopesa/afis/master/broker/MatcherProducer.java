package com.optimize.kopesa.afis.master.broker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.optimize.kopesa.afis.master.service.dto.MatcherRequestDTO;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;


@Component
public class MatcherProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public MatcherProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendToMatcherService(MatcherRequestDTO request) throws JsonProcessingException {
        kafkaTemplate.send("afis-matcher-topic", new ObjectMapper().writeValueAsString(request));
    }

}
