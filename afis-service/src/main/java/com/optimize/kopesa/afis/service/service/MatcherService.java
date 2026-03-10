package com.optimize.kopesa.afis.service.service;

import com.optimize.kopesa.afis.service.service.dto.MatcherRequestDTO;
import com.optimize.kopesa.afis.service.service.dto.MatcherResponseDTO;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MatcherService {

    private final MessageBrokerService brokerService;
    private final FingerprintStoreService fingerprintStoreService;

    public MatcherService(MessageBrokerService brokerService, FingerprintStoreService fingerprintStoreService) {
        this.brokerService = brokerService;
        this.fingerprintStoreService = fingerprintStoreService;
    }

    private MatcherResponseDTO findMatch(MatcherRequestDTO matcherRequestDTO) {
        Pageable pageable = PageRequest.of(matcherRequestDTO.getBatchId(), matcherRequestDTO.getBatchSize());
        List<Double> scores = new ArrayList<>();
        return fingerprintStoreService.findAll(pageable).stream()
            .parallel()
            .map(candidate -> {
                matcherRequestDTO.getMatchers().forEach(matcher -> scores.add(Optional.ofNullable(matcher).map(m -> m.match(candidate.getTemplate())).orElse(0d)));
                return new AbstractMap.SimpleEntry<>(candidate.getRid(), scores.stream().mapToDouble(Double::doubleValue).max().getAsDouble());
            })
            .filter(entry -> entry.getValue() >= matcherRequestDTO.getThreshold())
            .max(Map.Entry.comparingByValue())
            .map(entry -> new MatcherResponseDTO(matcherRequestDTO.getRid(), matcherRequestDTO.getBatchId(), entry.getValue(), Boolean.TRUE, entry.getKey()))
            .orElse(new MatcherResponseDTO(matcherRequestDTO.getRid(), matcherRequestDTO.getBatchId(), null, Boolean.FALSE, null));
    }

    @KafkaListener(topics = "afis-matcher-topic", groupId = "afis-master", containerFactory = "kafkaListenerContainerFactory")
    public void processBatchRequest(MatcherRequestDTO matcherRequestDTO) {
        MatcherResponseDTO result = findMatch(matcherRequestDTO);
        // Envoie du résultat au service principal
        brokerService.sendResult(result);
    }
}
