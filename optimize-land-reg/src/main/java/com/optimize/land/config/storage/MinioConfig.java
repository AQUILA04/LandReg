package com.optimize.land.config.storage;

import com.optimize.common.blob.BlobStorageService;
import com.optimize.common.blob.StorageBuckets;
import io.minio.MinioClient;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "landreg.storage", name = "enabled", havingValue = "true")
@RequiredArgsConstructor
public class MinioConfig {

    private final MinioProperties properties;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
            .endpoint(properties.getEndpoint())
            .credentials(properties.getAccessKey(), properties.getSecretKey())
            .build();
    }

    @Bean
    public BlobStorageService blobStorageService(MinioClient minioClient) {
        BlobStorageService service = new BlobStorageService(minioClient);
        service.ensureBuckets(StorageBuckets.QUEUE_PROCESSING, StorageBuckets.STORE);
        return service;
    }

    @PostConstruct
    void logActivation() {
        // Bean presence confirms MinIO wiring when enabled
    }
}
