package com.optimize.land.job;

import com.optimize.land.client.AfisClient;
import com.optimize.land.model.entity.FingerprintStore;
import com.optimize.land.model.entity.OutboxEvent;
import com.optimize.land.repository.OutboxEventRepository;
import com.optimize.land.service.FingerprintStoreService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OutboxRetryJobTest {

    @Mock
    private OutboxEventRepository outboxEventRepository;

    @Mock
    private FingerprintStoreService fingerprintStoreService;

    @Mock
    private AfisClient afisClient;

    @InjectMocks
    private OutboxRetryJob outboxRetryJob;

    private OutboxEvent pendingEvent;

    @BeforeEach
    void setUp() {
        pendingEvent = new OutboxEvent("RID-123", "LEGAL_ENTITY_FINGERPRINT_SYNC", "PENDING");
    }

    @Test
    void retryPendingOutboxEvents_Success_MarksAsProcessed() {
        // Arrange
        when(outboxEventRepository.findByStatus("PENDING")).thenReturn(List.of(pendingEvent));

        Set<FingerprintStore> fingerprints = new HashSet<>();
        fingerprints.add(new FingerprintStore());
        when(fingerprintStoreService.getByRid("RID-123")).thenReturn(fingerprints);

        when(afisClient.sendLegalEntityFingerprint(fingerprints)).thenReturn("SUCCESS_RID");

        // Act
        outboxRetryJob.retryPendingOutboxEvents();

        // Assert
        assertEquals("PROCESSED", pendingEvent.getStatus());
        verify(outboxEventRepository).save(pendingEvent);
    }

    @Test
    void retryPendingOutboxEvents_StillFailing_StaysPending() {
        // Arrange
        when(outboxEventRepository.findByStatus("PENDING")).thenReturn(List.of(pendingEvent));

        Set<FingerprintStore> fingerprints = new HashSet<>();
        fingerprints.add(new FingerprintStore());
        when(fingerprintStoreService.getByRid("RID-123")).thenReturn(fingerprints);

        when(afisClient.sendLegalEntityFingerprint(fingerprints)).thenReturn("FALLBACK_PENDING");

        // Act
        outboxRetryJob.retryPendingOutboxEvents();

        // Assert
        assertEquals("PENDING", pendingEvent.getStatus());
        verify(outboxEventRepository, never()).save(any(OutboxEvent.class));
    }

    @Test
    void retryPendingOutboxEvents_NoFingerprints_MarksAsProcessedAndSkipsCall() {
        // Arrange
        when(outboxEventRepository.findByStatus("PENDING")).thenReturn(List.of(pendingEvent));
        when(fingerprintStoreService.getByRid("RID-123")).thenReturn(Collections.emptySet());

        // Act
        outboxRetryJob.retryPendingOutboxEvents();

        // Assert
        assertEquals("PROCESSED", pendingEvent.getStatus());
        verify(afisClient, never()).sendLegalEntityFingerprint(any());
        verify(outboxEventRepository).save(pendingEvent);
    }
}
