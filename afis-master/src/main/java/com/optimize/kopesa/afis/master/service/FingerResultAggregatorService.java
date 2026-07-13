package com.optimize.kopesa.afis.master.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.optimize.common.blob.ImageUriResolver;
import com.optimize.common.blob.StorageBuckets;
import com.optimize.common.blob.kafka.FingerWorkerResponse;
import com.optimize.kopesa.afis.master.broker.MasterFeedbackProducer;
import com.optimize.kopesa.afis.master.domain.FingerprintStore;
import com.optimize.kopesa.afis.master.domain.MatcherJobHistory;
import com.optimize.kopesa.afis.master.domain.ProcessingFingerprint;
import com.optimize.kopesa.afis.master.domain.enumeration.ActorType;
import com.optimize.kopesa.afis.master.domain.enumeration.MatchJobStatus;
import com.optimize.kopesa.afis.master.repository.FingerprintStoreRepository;
import com.optimize.kopesa.afis.master.repository.ProcessingFingerprintRepository;
import com.optimize.kopesa.afis.master.service.dto.RegistrationProcessorFeedback;
import com.optimize.kopesa.afis.master.service.storage.BlobArchivalService;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class FingerResultAggregatorService {

    private static final Logger log = LoggerFactory.getLogger(FingerResultAggregatorService.class);

    private final MatcherJobHistoryService matcherJobHistoryService;
    private final ProcessingFingerprintRepository processingFingerprintRepository;
    private final FingerprintStoreRepository fingerprintStoreRepository;
    private final BlobArchivalService blobArchivalService;
    private final MasterFeedbackProducer feedbackProducer;

    public FingerResultAggregatorService(
        MatcherJobHistoryService matcherJobHistoryService,
        ProcessingFingerprintRepository processingFingerprintRepository,
        FingerprintStoreRepository fingerprintStoreRepository,
        BlobArchivalService blobArchivalService,
        MasterFeedbackProducer feedbackProducer
    ) {
        this.matcherJobHistoryService = matcherJobHistoryService;
        this.processingFingerprintRepository = processingFingerprintRepository;
        this.fingerprintStoreRepository = fingerprintStoreRepository;
        this.blobArchivalService = blobArchivalService;
        this.feedbackProducer = feedbackProducer;
    }

    public MatcherJobHistory handleFingerResponse(FingerWorkerResponse response) throws JsonProcessingException {
        updateProcessingFingerprint(response);
        MatcherJobHistory history = matcherJobHistoryService.updateFingerResponse(response);
        if (history != null && MatchJobStatus.FINISHED.equals(history.getStatus())) {
            finalizeJob(history);
        }
        return history;
    }

    private void updateProcessingFingerprint(FingerWorkerResponse response) {
        processingFingerprintRepository
            .findByRid(response.getRid())
            .stream()
            .filter(p -> Objects.equals(p.getFingerId(), response.getFingerId()))
            .findFirst()
            .ifPresent(processing -> {
                if (response.getFingerprintTemplateBase64() != null) {
                    processing.setFingerprintTemplate(Base64.getDecoder().decode(response.getFingerprintTemplateBase64()));
                }
                processing.setQdrantPointId(response.getQdrantPointId());
                processingFingerprintRepository.save(processing);
            });
    }

    private void finalizeJob(MatcherJobHistory history) throws JsonProcessingException {
        String rid = history.getRid();
        List<ProcessingFingerprint> processes = processingFingerprintRepository.findByRid(rid);
        if (Boolean.TRUE.equals(history.getFoundMatch())) {
            log.info("Duplicate detected for rid={}, cleaning queue storage", rid);
            blobArchivalService.deleteQueueRid(rid);
            processingFingerprintRepository.deleteAll(processes);
            feedbackProducer.sendFeedbackToRegistrationProcessor(
                new RegistrationProcessorFeedback(rid, Boolean.TRUE, history.getMatchedRID())
            );
            return;
        }
        log.info("Unique rid={}, promoting storage and persisting templates", rid);
        blobArchivalService.promoteRid(rid);
        for (ProcessingFingerprint processing : processes) {
            FingerprintStore store = toFingerprintStore(processing);
            fingerprintStoreRepository.save(store);
        }
        processingFingerprintRepository.deleteAll(processes);
        feedbackProducer.sendFeedbackToRegistrationProcessor(
            new RegistrationProcessorFeedback(rid, Boolean.FALSE, null)
        );
    }

    private FingerprintStore toFingerprintStore(ProcessingFingerprint processing) {
        FingerprintStore store = new FingerprintStore();
        store.setId(UUID.randomUUID().toString());
        store.setRid(processing.getRid());
        store.setHandType(processing.getHandType());
        store.setFingerName(processing.getFingerName());
        store.setType(ActorType.PERSON);
        store.setFingerprintImageContentType(processing.getFingerprintImageContentType());
        store.setFingerId(processing.getFingerId());
        store.setFingerprintTemplate(processing.getFingerprintTemplate());
        store.setQdrantPointId(processing.getQdrantPointId());
        String objectKey = resolveStoreObjectKey(processing);
        if (objectKey != null) {
            store.setImageObjectKey(objectKey);
            store.setImageBucket(StorageBuckets.STORE);
            store.setImageUri("s3://" + StorageBuckets.STORE + "/" + objectKey);
        } else if (processing.getFingerprintImage() != null && processing.getFingerprintImage().length > 0) {
            store.setFingerprintImage(processing.getFingerprintImage());
        }
        return store;
    }

    private static String resolveStoreObjectKey(ProcessingFingerprint processing) {
        String objectKey = processing.getImageObjectKey();
        if (objectKey == null && processing.getImageUri() != null && !processing.getImageUri().isBlank()) {
            objectKey = ImageUriResolver.parse(processing.getImageUri()).objectKey();
        }
        if (objectKey == null && processing.getRid() != null && processing.getFingerId() != null) {
            objectKey =
                processing.getRid() +
                "/" +
                processing.getFingerId() +
                ImageUriResolver.extensionFromContentType(processing.getFingerprintImageContentType());
        }
        return objectKey;
    }
}
