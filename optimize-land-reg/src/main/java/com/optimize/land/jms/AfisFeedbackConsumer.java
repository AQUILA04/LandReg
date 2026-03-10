package com.optimize.land.jms;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.optimize.common.entities.exception.ResourceNotFoundException;
import com.optimize.land.jms.model.RegistrationProcessorFeedback;
import com.optimize.land.model.entity.FingerprintMatchingHistory;
import com.optimize.land.service.ActorService;
import com.optimize.land.service.FingerprintMatchingHistoryService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContextException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class AfisFeedbackConsumer {
    private final ActorService actorService;
    private final FingerprintMatchingHistoryService fingerprintMatchingHistoryService;


    @KafkaListener(topics = "afis-master-feedback-topic", groupId = "afis-master", containerFactory = "kafkaListenerContainerFactory")
    public void receiveAFISFeedback (String message) throws JsonProcessingException {
        log.info("RECEIVING MATCHING FEEDBACK: {}", message );
        RegistrationProcessorFeedback feedback = new ObjectMapper().readValue(message, RegistrationProcessorFeedback.class);
        try {
            if (fingerprintMatchingHistoryService.feedbackUpdate(feedback) && actorService.existsByRid(feedback.getRid())) {
                log.info("AFTER MATCHING OPERATION STARTING...");
                actorService.afterMatchingOperation(feedback);
            } else {
                log.error("===> FINGERPRINT MATCHING FEEDBACK PROCESSING NOT FOUND {}", feedback);
                throw new ApplicationContextException("FINGERPRINT MATCHING FEEDBACK PROCESSING NOT FOUND : "+ feedback);
            }
        } catch (Exception e) {
            log.error("MATCHING FEEDBACK ERROR: {}", e.getLocalizedMessage());
        }
    }
}
