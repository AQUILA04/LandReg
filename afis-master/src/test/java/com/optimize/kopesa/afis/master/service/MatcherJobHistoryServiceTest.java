package com.optimize.kopesa.afis.master.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.optimize.common.blob.kafka.FingerMatchStatus;
import com.optimize.common.blob.kafka.FingerWorkerResponse;
import com.optimize.kopesa.afis.master.domain.MatcherJobHistory;
import com.optimize.kopesa.afis.master.domain.enumeration.MatchJobStatus;
import com.optimize.kopesa.afis.master.repository.MatcherJobHistoryRepository;
import com.optimize.kopesa.afis.master.service.mapper.MatcherJobHistoryMapper;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MatcherJobHistoryServiceTest {

    @Mock
    private MatcherJobHistoryRepository matcherJobHistoryRepository;

    @Mock
    private MatcherJobHistoryMapper matcherJobHistoryMapper;

    private MatcherJobHistoryService service;

    @BeforeEach
    void setUp() {
        service = new MatcherJobHistoryService(matcherJobHistoryRepository, matcherJobHistoryMapper);
    }

    @Test
    void updateFingerResponse_doesNotFinishEarlyOnDuplicate() {
        MatcherJobHistory history = new MatcherJobHistory();
        history.setRid("RID-1");
        history.setProducerCount(3);
        history.setConsumerReponseCount(0);
        history.setHighScore(0d);
        history.setFoundMatch(Boolean.FALSE);
        when(matcherJobHistoryRepository.findByRid("RID-1")).thenReturn(Optional.of(history));
        when(matcherJobHistoryRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        FingerWorkerResponse duplicate = new FingerWorkerResponse();
        duplicate.setRid("RID-1");
        duplicate.setStatus(FingerMatchStatus.DUPLICATE);
        duplicate.setMatchedRid("RID-EXISTING");
        duplicate.setHighestScore(90d);

        MatcherJobHistory result = service.updateFingerResponse(duplicate);

        assertTrue(result.getFoundMatch());
        assertEquals("RID-EXISTING", result.getMatchedRID());
        assertEquals(1, result.getConsumerReponseCount());
        assertFalse(MatchJobStatus.FINISHED.equals(result.getStatus()));
    }

    @Test
    void updateFingerResponse_finishesWhenAllResponsesReceivedAfterDuplicate() {
        MatcherJobHistory history = new MatcherJobHistory();
        history.setRid("RID-1");
        history.setProducerCount(2);
        history.setConsumerReponseCount(1);
        history.setHighScore(90d);
        history.setFoundMatch(Boolean.TRUE);
        history.setMatchedRID("RID-EXISTING");
        when(matcherJobHistoryRepository.findByRid("RID-1")).thenReturn(Optional.of(history));
        when(matcherJobHistoryRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        FingerWorkerResponse secondFinger = new FingerWorkerResponse();
        secondFinger.setRid("RID-1");
        secondFinger.setStatus(FingerMatchStatus.UNIQUE);
        secondFinger.setHighestScore(10d);

        MatcherJobHistory result = service.updateFingerResponse(secondFinger);

        assertEquals(2, result.getConsumerReponseCount());
        assertEquals(MatchJobStatus.FINISHED, result.getStatus());
    }

    @Test
    void updateFingerResponse_finishesUniqueJobWhenAllResponsesReceived() {
        MatcherJobHistory history = new MatcherJobHistory();
        history.setRid("RID-1");
        history.setProducerCount(1);
        history.setConsumerReponseCount(0);
        history.setHighScore(0d);
        history.setFoundMatch(Boolean.FALSE);
        when(matcherJobHistoryRepository.findByRid("RID-1")).thenReturn(Optional.of(history));
        when(matcherJobHistoryRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        FingerWorkerResponse unique = new FingerWorkerResponse();
        unique.setRid("RID-1");
        unique.setStatus(FingerMatchStatus.UNIQUE);
        unique.setHighestScore(5d);

        MatcherJobHistory result = service.updateFingerResponse(unique);

        assertEquals(MatchJobStatus.FINISHED, result.getStatus());
        assertFalse(result.getFoundMatch());
    }
}
