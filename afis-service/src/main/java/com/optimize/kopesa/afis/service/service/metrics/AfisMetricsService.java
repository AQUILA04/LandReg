package com.optimize.kopesa.afis.service.service.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Service;

@Service
public class AfisMetricsService {

    private final MeterRegistry meterRegistry;

    public AfisMetricsService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public void recordS3Fetch(long durationMs) {
        timer("afis.s3.fetch.time").record(durationMs, TimeUnit.MILLISECONDS);
    }

    public void recordVectorize(long durationMs) {
        timer("afis.vectorize.time").record(durationMs, TimeUnit.MILLISECONDS);
    }

    public void recordHnsw(long durationMs) {
        timer("afis.hnsw.time").record(durationMs, TimeUnit.MILLISECONDS);
    }

    public void recordSourceAfis(long durationMs) {
        timer("afis.sourceafis.time").record(durationMs, TimeUnit.MILLISECONDS);
    }

    public void recordPipelineTotal(long durationMs) {
        timer("afis.pipeline.total.time").record(durationMs, TimeUnit.MILLISECONDS);
    }

    public void recordKafkaMessageSize(int bytes) {
        meterRegistry.summary("afis.kafka.message.size").record(bytes);
    }

    private Timer timer(String name) {
        return Timer.builder(name).register(meterRegistry);
    }
}
