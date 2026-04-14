package com.optimize.land.jms;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.optimize.land.jms.model.RegistrationProcessorFeedback;
import com.optimize.land.service.ActorService;
import com.optimize.land.service.FingerprintMatchingHistoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.context.ApplicationContextException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AfisFeedbackConsumerTest {

    @Mock
    private ActorService actorService;

    @Mock
    private FingerprintMatchingHistoryService fingerprintMatchingHistoryService;

    @Mock
    private Acknowledgment acknowledgment;

    @InjectMocks
    private AfisFeedbackConsumer consumer;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void receiveAFISFeedback_Success_AcknowledgesMessage() throws Exception {
        // Arrange
        RegistrationProcessorFeedback feedback = new RegistrationProcessorFeedback();
        feedback.setRid("RID-123");
        String message = objectMapper.writeValueAsString(feedback);

        when(fingerprintMatchingHistoryService.feedbackUpdate(any())).thenReturn(true);
        when(actorService.existsByRid("RID-123")).thenReturn(true);

        // Act
        consumer.receiveAFISFeedback(message, acknowledgment);

        // Assert
        verify(actorService).afterMatchingOperation(any());
        verify(acknowledgment).acknowledge(); // Ensure manual ack is called
    }

    @Test
    void receiveAFISFeedback_ProcessingNotFound_ThrowsException() throws Exception {
        // Arrange
        RegistrationProcessorFeedback feedback = new RegistrationProcessorFeedback();
        feedback.setRid("RID-456");
        String message = objectMapper.writeValueAsString(feedback);

        when(fingerprintMatchingHistoryService.feedbackUpdate(any())).thenReturn(false);

        // Act & Assert
        assertThrows(ApplicationContextException.class, () -> {
            consumer.receiveAFISFeedback(message, acknowledgment);
        });

        // Assert no ack
        verify(actorService, never()).afterMatchingOperation(any());
        verify(acknowledgment, never()).acknowledge();
    }

    @Test
    void receiveAFISFeedback_ExceptionDuringProcessing_RethrowsException() throws Exception {
        // Arrange
        RegistrationProcessorFeedback feedback = new RegistrationProcessorFeedback();
        feedback.setRid("RID-789");
        String message = objectMapper.writeValueAsString(feedback);

        when(fingerprintMatchingHistoryService.feedbackUpdate(any())).thenReturn(true);
        when(actorService.existsByRid("RID-789")).thenReturn(true);
        doThrow(new RuntimeException("Database error")).when(actorService).afterMatchingOperation(any());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            consumer.receiveAFISFeedback(message, acknowledgment);
        });

        // Assert no ack
        verify(acknowledgment, never()).acknowledge();
    }
}
