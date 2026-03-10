package com.optimize.land.jms.config;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class KafkaTopicConfig {

    private final KafkaConfigProperties kafkaConfigProperties;

    //TODO: add num partition config to application.yml
    @Bean
    public NewTopic afisMasterTopic() {
        return new NewTopic("afis-master-topic",
                kafkaConfigProperties
                        .getNumPartitions().get("afis-master-topic"),
                (short) (int) kafkaConfigProperties
                        .getReplicationFactor().get("afis-master-topic"));
    }

    @Bean
    public NewTopic afisMatcherTopic() {
        return new NewTopic("afis-matcher-topic", kafkaConfigProperties
                .getNumPartitions().get("afis-matcher-topic"),
                (short) (int) kafkaConfigProperties
                        .getReplicationFactor().get("afis-matcher-topic"));
    }

    @Bean
    public NewTopic afisMatcherResultTopic() {
        return new NewTopic("afis-matcher-result-topic",
                kafkaConfigProperties
                        .getNumPartitions().get("afis-matcher-result-topic"),
                (short) (int) kafkaConfigProperties
                        .getReplicationFactor().get("afis-matcher-result-topic"));
    }

    @Bean
    public NewTopic afisMasterFeedbackTopic() {
        return new NewTopic("afis-master-feedback-topic",
                kafkaConfigProperties
                        .getNumPartitions().get("afis-master-feedback-topic"),
                (short) (int) kafkaConfigProperties
                        .getReplicationFactor().get("afis-master-feedback-topic"));
    }
}
