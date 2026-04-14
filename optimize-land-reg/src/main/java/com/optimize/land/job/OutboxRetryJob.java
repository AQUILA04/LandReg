package com.optimize.land.job;

import com.optimize.land.client.AfisClient;
import com.optimize.land.model.entity.FingerprintStore;
import com.optimize.land.model.entity.OutboxEvent;
import com.optimize.land.repository.OutboxEventRepository;
import com.optimize.land.service.FingerprintStoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxRetryJob {

    private final OutboxEventRepository outboxEventRepository;
    private final FingerprintStoreService fingerprintStoreService;
    private final AfisClient afisClient;

    @Scheduled(fixedDelay = 300000) // Runs every 5 minutes
    @Transactional
    public void retryPendingOutboxEvents() {
        log.info("Starting Outbox Retry Job");
        List<OutboxEvent> pendingEvents = outboxEventRepository.findByStatus("PENDING");

        for (OutboxEvent event : pendingEvents) {
            if ("LEGAL_ENTITY_FINGERPRINT_SYNC".equals(event.getEventType())) {
                log.info("Retrying Outbox Event for RID: {}", event.getRid());
                try {
                    Set<FingerprintStore> fingerprintStores = fingerprintStoreService.getByRid(event.getRid());
                    if (fingerprintStores == null || fingerprintStores.isEmpty()) {
                        log.warn("No fingerprints found for RID: {}. Marking event as PROCESSED.", event.getRid());
                        event.setStatus("PROCESSED");
                        outboxEventRepository.save(event);
                        continue;
                    }

                    String response = afisClient.sendLegalEntityFingerprint(fingerprintStores);
                    if (!"FALLBACK_PENDING".equals(response)) {
                        log.info("Successfully synced fingerprints for RID: {}", event.getRid());
                        event.setStatus("PROCESSED");
                        outboxEventRepository.save(event);
                    } else {
                        log.warn("AFIS API still unavailable for RID: {}", event.getRid());
                    }
                } catch (Exception e) {
                    log.error("Error while retrying Outbox Event for RID: {}. Reason: {}", event.getRid(), e.getMessage());
                }
            }
        }
        log.info("Finished Outbox Retry Job");
    }
}
