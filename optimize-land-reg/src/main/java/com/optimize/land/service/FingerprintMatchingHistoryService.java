package com.optimize.land.service;

import com.optimize.common.entities.repository.GenericRepository;
import com.optimize.common.entities.service.GenericService;
import com.optimize.land.jms.model.RegistrationProcessorFeedback;
import com.optimize.land.model.entity.FingerprintMatchingHistory;
import com.optimize.land.repository.FingerprintMatchingHistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(readOnly = true)
public class FingerprintMatchingHistoryService extends GenericService<FingerprintMatchingHistory, Long> {

    protected FingerprintMatchingHistoryService(FingerprintMatchingHistoryRepository repository) {
        super(repository);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, noRollbackFor = Exception.class)
    public boolean feedbackUpdate(RegistrationProcessorFeedback feedback) {
        FingerprintMatchingHistory history = getRepository().getByRid(feedback.getRid());
        update(history.addFeedback(feedback));
        return Boolean.TRUE;
    }

    @Override
    public FingerprintMatchingHistoryRepository getRepository() {
        return (FingerprintMatchingHistoryRepository) super.getRepository();
    }
}
