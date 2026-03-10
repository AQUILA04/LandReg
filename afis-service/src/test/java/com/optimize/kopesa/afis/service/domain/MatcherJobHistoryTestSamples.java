package com.optimize.kopesa.afis.service.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class MatcherJobHistoryTestSamples {

    private static final Random random = new Random();
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static MatcherJobHistory getMatcherJobHistorySample1() {
        return new MatcherJobHistory().id("id1").rid("rid1").producerCount(1).consumerReponseCount(1).matchedRID("matchedRID1");
    }

    public static MatcherJobHistory getMatcherJobHistorySample2() {
        return new MatcherJobHistory().id("id2").rid("rid2").producerCount(2).consumerReponseCount(2).matchedRID("matchedRID2");
    }

    public static MatcherJobHistory getMatcherJobHistoryRandomSampleGenerator() {
        return new MatcherJobHistory()
            .id(UUID.randomUUID().toString())
            .rid(UUID.randomUUID().toString())
            .producerCount(intCount.incrementAndGet())
            .consumerReponseCount(intCount.incrementAndGet())
            .matchedRID(UUID.randomUUID().toString());
    }
}
