package com.optimize.land.service;

import com.optimize.land.client.AfisClient;
import com.optimize.land.model.entity.FingerprintStore;
import com.optimize.land.model.entity.OutboxEvent;
import com.optimize.land.model.entity.Registration;
import com.optimize.land.repository.OutboxEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActorServiceOutboxTest {

    @Mock
    private AfisClient afisClient;

    @Mock
    private OutboxEventRepository outboxEventRepository;

    @Mock
    private SynchroHistoryService synchroHistoryService;

    @InjectMocks
    private ActorService actorService; // Will inject mocks into the constructor

    @BeforeEach
    void setUp() {
        // Need to set up basic mocks for the spied ActorService or use a subclass if required.
        // For simplicity, we just mock the required parts.
        // Mocking the abstract generic repository from parent is tricky without full setup,
        // so we'll spy on ActorService to mock `validate`.
        actorService = spy(actorService);
        doNothing().when(actorService).validate(anyString());
    }

    @Test
    void validateLegalEntity_AfisUnavailable_SavesToOutbox() {
        // Arrange
        Registration registration = new Registration();
        registration.setRid("RID-123");
        Set<FingerprintStore> stores = new HashSet<>();
        stores.add(new FingerprintStore());
        registration.setFingerprintStores(stores);

        when(afisClient.sendLegalEntityFingerprint(stores)).thenReturn("FALLBACK_PENDING");

        // Act
        actorService.validateLegalEntity(registration);

        // Assert
        ArgumentCaptor<OutboxEvent> captor = ArgumentCaptor.forClass(OutboxEvent.class);
        verify(outboxEventRepository).save(captor.capture());

        OutboxEvent savedEvent = captor.getValue();
        assertEquals("RID-123", savedEvent.getRid());
        assertEquals("LEGAL_ENTITY_FINGERPRINT_SYNC", savedEvent.getEventType());
        assertEquals("PENDING", savedEvent.getStatus());

        verify(actorService).validate("RID-123");
    }
}
