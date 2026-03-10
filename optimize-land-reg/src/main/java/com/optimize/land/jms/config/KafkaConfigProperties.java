package com.optimize.land.jms.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "lang-reg.kafka.config")
@Getter
@Setter
public class KafkaConfigProperties {
    private Map<String, Integer> numPartitions = new HashMap<>();
    private Map<String, Integer> replicationFactor = new HashMap<>();
}
