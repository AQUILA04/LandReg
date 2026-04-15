package com.optimize.kopesa.afis.master.broker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.optimize.kopesa.afis.master.domain.MatcherJobHistory;
import com.optimize.kopesa.afis.master.domain.ProcessingFingerprint;
import com.optimize.kopesa.afis.master.domain.enumeration.MatchJobStatus;
import com.optimize.kopesa.afis.master.repository.FingerprintStoreRepository;
import com.optimize.kopesa.afis.master.repository.ProcessingFingerprintRepository;
import com.optimize.kopesa.afis.master.service.MasterMatcherService;
import com.optimize.kopesa.afis.master.service.MatcherJobHistoryService;
import com.optimize.kopesa.afis.master.service.dto.AfisMasterRequest;
import com.optimize.kopesa.afis.master.service.dto.MatcherResponseDTO;
import com.optimize.kopesa.afis.master.service.dto.RegistrationProcessorFeedback;
import com.optimize.kopesa.afis.master.service.mapper.FingerprintStoreMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;


@Component
public class AfisMasterConsumer {
    Logger log = LoggerFactory.getLogger(AfisMasterConsumer.class);
    private final MasterMatcherService masterMatcherService;
    private final MatcherJobHistoryService matcherJobHistoryService;
    private final MasterFeedbackProducer feedbackProducer;
    private final ProcessingFingerprintRepository processingFingerprintRepository;
    private final FingerprintStoreMapper fingerprintStoreMapper;
    private final FingerprintStoreRepository fingerprintStoreRepository;


    public AfisMasterConsumer(MasterMatcherService masterMatcherService,
                              MatcherJobHistoryService matcherJobHistoryService,
                              MasterFeedbackProducer feedbackProducer,
                              ProcessingFingerprintRepository processingFingerprintRepository,
                              FingerprintStoreMapper fingerprintStoreMapper,
                              FingerprintStoreRepository fingerprintStoreRepository) {
        this.masterMatcherService = masterMatcherService;
        this.matcherJobHistoryService = matcherJobHistoryService;
        this.feedbackProducer = feedbackProducer;
        this.processingFingerprintRepository = processingFingerprintRepository;
        this.fingerprintStoreMapper = fingerprintStoreMapper;
        this.fingerprintStoreRepository = fingerprintStoreRepository;
    }

    @KafkaListener(topics = "afis-master-topic", groupId = "afis-master", containerFactory = "kafkaListenerContainerFactory")
    public void receiveMasterRequest(String message, Acknowledgment acknowledgment) throws JsonProcessingException {
        try {
            log.info("RECEIVING MATCHING REQUEST: {}", message.substring(0, 255) );
            AfisMasterRequest request = new ObjectMapper().readValue(message, AfisMasterRequest.class);
            log.info("MATCHING REQUEST RECEIVED SIZE: {}", request.getFingerprintStores().size());
            masterMatcherService.dispatchDeduplicationJob(request);
            log.info("MATCHING REQUEST DISPATCHED SIZE");
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("FAILED TO PROCESS MATCHING REQUEST: {}", e.getMessage(), e);
            throw e; // Rethrow to trigger DLQ / Retry logic
        }
    }

    @KafkaListener(topics = "afis-matcher-result-topic", groupId = "afis-master", containerFactory = "kafkaListenerContainerFactory")
    public void receiveMatcherServiceResponse(String message, Acknowledgment acknowledgment) throws JsonProcessingException {
        try {
            log.info("RECEIVING MATCHING RESPONSE: {}", message);
            MatcherResponseDTO response = new ObjectMapper().readValue(message, MatcherResponseDTO.class);
            MatcherJobHistory matcherJobHistory = matcherJobHistoryService.updateConsumerResponseJob(response);
            if (Objects.nonNull(matcherJobHistory) && MatchJobStatus.FINISHED.equals(matcherJobHistory.getStatus())) {
                log.info("MATCHING RESPONSE FINISHED: {}", matcherJobHistory.getRid());
                if (matcherJobHistory.getFoundMatch().equals(Boolean.FALSE)) {
                    log.info("MATCH FOUND FALSE");
                    List<ProcessingFingerprint> processes = processingFingerprintRepository.findByRid(
                        matcherJobHistory.getRid());
                    fingerprintStoreRepository.saveAll(
                        fingerprintStoreMapper.toFingerprintStores(processes));
                    log.info("MATCH FOUND FALSE SAVED");
                    processingFingerprintRepository.deleteAll(processes);
                }
                log.info("MATCHING RESPONSE FINISHED SENT FEEDBACK: {}", matcherJobHistory.getRid());
                feedbackProducer.sendFeedbackToRegistrationProcessor(new RegistrationProcessorFeedback(matcherJobHistory.getRid(),
                    matcherJobHistory.getFoundMatch(), matcherJobHistory.getMatchedRID()));
            }
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("FAILED TO PROCESS MATCHER SERVICE RESPONSE: {}", e.getMessage(), e);
            throw e; // Rethrow to trigger DLQ / Retry logic
        }
    }

}
