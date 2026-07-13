package com.optimize.kopesa.afis.master.broker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.optimize.common.blob.kafka.AfisMasterRequestV2;
import com.optimize.common.blob.kafka.FingerWorkerResponse;
import com.optimize.kopesa.afis.master.config.AfisPipelineProperties;
import com.optimize.kopesa.afis.master.domain.MatcherJobHistory;
import com.optimize.kopesa.afis.master.domain.ProcessingFingerprint;
import com.optimize.kopesa.afis.master.domain.enumeration.MatchJobStatus;
import com.optimize.kopesa.afis.master.repository.FingerprintStoreRepository;
import com.optimize.kopesa.afis.master.repository.ProcessingFingerprintRepository;
import com.optimize.kopesa.afis.master.service.FingerResultAggregatorService;
import com.optimize.kopesa.afis.master.service.MasterMatcherService;
import com.optimize.kopesa.afis.master.service.MatcherJobHistoryService;
import com.optimize.kopesa.afis.master.service.dto.AfisMasterRequest;
import com.optimize.kopesa.afis.master.service.dto.MatcherResponseDTO;
import com.optimize.kopesa.afis.master.service.dto.RegistrationProcessorFeedback;
import com.optimize.kopesa.afis.master.service.mapper.FingerprintStoreMapper;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
public class AfisMasterConsumer {

    public static final String TOPIC_MATCH_REQ = "biometrics.match.req";
    public static final String TOPIC_FINGER_RES = "biometrics.finger.res";

    Logger log = LoggerFactory.getLogger(AfisMasterConsumer.class);
    private final MasterMatcherService masterMatcherService;
    private final MatcherJobHistoryService matcherJobHistoryService;
    private final MasterFeedbackProducer feedbackProducer;
    private final ProcessingFingerprintRepository processingFingerprintRepository;
    private final FingerprintStoreMapper fingerprintStoreMapper;
    private final FingerprintStoreRepository fingerprintStoreRepository;
    private final FingerResultAggregatorService fingerResultAggregatorService;
    private final AfisPipelineProperties afisPipelineProperties;
    private final ObjectMapper objectMapper;

    public AfisMasterConsumer(
        MasterMatcherService masterMatcherService,
        MatcherJobHistoryService matcherJobHistoryService,
        MasterFeedbackProducer feedbackProducer,
        ProcessingFingerprintRepository processingFingerprintRepository,
        FingerprintStoreMapper fingerprintStoreMapper,
        FingerprintStoreRepository fingerprintStoreRepository,
        FingerResultAggregatorService fingerResultAggregatorService,
        AfisPipelineProperties afisPipelineProperties,
        ObjectMapper objectMapper
    ) {
        this.masterMatcherService = masterMatcherService;
        this.matcherJobHistoryService = matcherJobHistoryService;
        this.feedbackProducer = feedbackProducer;
        this.processingFingerprintRepository = processingFingerprintRepository;
        this.fingerprintStoreMapper = fingerprintStoreMapper;
        this.fingerprintStoreRepository = fingerprintStoreRepository;
        this.fingerResultAggregatorService = fingerResultAggregatorService;
        this.afisPipelineProperties = afisPipelineProperties;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = { "afis-master-topic", TOPIC_MATCH_REQ }, groupId = "afis-master", containerFactory = "kafkaListenerContainerFactory")
    public void receiveMasterRequest(String message, Acknowledgment acknowledgment) throws JsonProcessingException {
        try {
            log.info("RECEIVING MATCHING REQUEST");
            JsonNode root = objectMapper.readTree(message);
            if (root.has("schemaVersion") && root.get("schemaVersion").asInt() >= AfisMasterRequestV2.SCHEMA_VERSION) {
                AfisMasterRequestV2 request = objectMapper.readValue(message, AfisMasterRequestV2.class);
                masterMatcherService.dispatchDeduplicationJobV2(request);
            } else if ("v2".equalsIgnoreCase(afisPipelineProperties.getVersion()) && root.has("fingers")) {
                AfisMasterRequestV2 request = objectMapper.readValue(message, AfisMasterRequestV2.class);
                masterMatcherService.dispatchDeduplicationJobV2(request);
            } else {
                AfisMasterRequest request = objectMapper.readValue(message, AfisMasterRequest.class);
                masterMatcherService.dispatchDeduplicationJob(request);
            }
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("FAILED TO PROCESS MATCHING REQUEST: {}", e.getMessage(), e);
            throw e;
        }
    }

    @KafkaListener(topics = TOPIC_FINGER_RES, groupId = "afis-master-finger", containerFactory = "kafkaListenerContainerFactory")
    public void receiveFingerResponse(String message, Acknowledgment acknowledgment) throws JsonProcessingException {
        try {
            FingerWorkerResponse response = objectMapper.readValue(message, FingerWorkerResponse.class);
            fingerResultAggregatorService.handleFingerResponse(response);
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("FAILED TO PROCESS FINGER RESPONSE: {}", e.getMessage(), e);
            throw e;
        }
    }

    @KafkaListener(topics = "afis-matcher-result-topic", groupId = "afis-master", containerFactory = "kafkaListenerContainerFactory")
    public void receiveMatcherServiceResponse(String message, Acknowledgment acknowledgment) throws JsonProcessingException {
        try {
            log.info("RECEIVING MATCHING RESPONSE: {}", message);
            MatcherResponseDTO response = objectMapper.readValue(message, MatcherResponseDTO.class);
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
            throw e;
        }
    }
}
