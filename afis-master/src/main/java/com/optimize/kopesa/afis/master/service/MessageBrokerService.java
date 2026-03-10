package com.optimize.kopesa.afis.master.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.optimize.kopesa.afis.master.broker.MatcherProducer;
import com.optimize.kopesa.afis.master.service.dto.MatcherRequestDTO;
import org.springframework.stereotype.Service;


@Service
public class MessageBrokerService {
    private final MatcherProducer matcherProducer;

    public MessageBrokerService(MatcherProducer matcherProducer) {
        this.matcherProducer = matcherProducer;
    }

    public void sendBatchRequest(MatcherRequestDTO request) throws JsonProcessingException {
        matcherProducer.sendToMatcherService(request);
    }
}
