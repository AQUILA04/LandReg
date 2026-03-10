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
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;


@Component
public class AfisMasterConsumer {
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
    public void receiveMasterRequest(String message) throws JsonProcessingException {
        AfisMasterRequest request = new ObjectMapper().readValue(message, AfisMasterRequest.class);
        masterMatcherService.dispatchDeduplicationJob(request);
    }

    @KafkaListener(topics = "afis-matcher-result-topic", groupId = "afis-master", containerFactory = "kafkaListenerContainerFactory")
    public void receiveMatcherServiceResponse(String message) throws JsonProcessingException {
        MatcherResponseDTO response = new ObjectMapper().readValue(message, MatcherResponseDTO.class);
        MatcherJobHistory matcherJobHistory = matcherJobHistoryService.updateConsumerResponseJob(response);
        if (Objects.nonNull(matcherJobHistory) && MatchJobStatus.FINISHED.equals(matcherJobHistory.getStatus())) {
            if (matcherJobHistory.getFoundMatch().equals(Boolean.FALSE)) {
                List<ProcessingFingerprint> processes = processingFingerprintRepository.findByRid(
                    matcherJobHistory.getRid());
                fingerprintStoreRepository.saveAll(
                    fingerprintStoreMapper.toFingerprintStores(processes));
                processingFingerprintRepository.deleteAll(processes);
            }
            feedbackProducer.sendFeedbackToRegistrationProcessor(new RegistrationProcessorFeedback(matcherJobHistory.getRid(),
                matcherJobHistory.getFoundMatch(), matcherJobHistory.getMatchedRID()));
        }
    }

}
