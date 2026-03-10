package com.optimize.kopesa.afis.master.repository;

import com.optimize.kopesa.afis.master.domain.MatcherJobHistory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data MongoDB repository for the MatcherJobHistory entity.
 */
@Repository
public interface MatcherJobHistoryRepository extends MongoRepository<MatcherJobHistory, String> {

    Optional<MatcherJobHistory> findByRid(String rid);
}
