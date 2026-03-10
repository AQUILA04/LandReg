package com.optimize.kopesa.afis.master.broker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.optimize.kopesa.afis.master.service.dto.RegistrationProcessorFeedback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class MasterFeedbackProducer {
    private final Logger log = LoggerFactory.getLogger(MasterFeedbackProducer.class);
    private final KafkaTemplate<String, String> kafkaTemplate;

    public MasterFeedbackProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendFeedbackToRegistrationProcessor (RegistrationProcessorFeedback feedback) throws JsonProcessingException {
        log.info("SENDING FEEDBACK: {}", feedback);
        kafkaTemplate.send("afis-master-feedback-topic", new ObjectMapper().writeValueAsString(feedback) );
        log.info("FEEDBACK SENT: {}", feedback);
    }
}
