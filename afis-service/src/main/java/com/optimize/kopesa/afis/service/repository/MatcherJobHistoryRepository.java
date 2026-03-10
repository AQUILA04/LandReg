package com.optimize.kopesa.afis.service.repository;

import com.optimize.kopesa.afis.service.domain.MatcherJobHistory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the MatcherJobHistory entity.
 */
@Repository
public interface MatcherJobHistoryRepository extends MongoRepository<MatcherJobHistory, String> {}
