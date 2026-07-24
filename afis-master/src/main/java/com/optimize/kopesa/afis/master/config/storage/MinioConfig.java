package com.optimize.kopesa.afis.master.config.storage;

import com.optimize.common.blob.BlobStorageService;
import com.optimize.common.blob.StorageBuckets;
import com.optimize.kopesa.afis.master.config.AfisPipelineProperties;
import io.minio.MinioClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "afis.pipeline.storage", name = "enabled", havingValue = "true", matchIfMissing = true)
public class MinioConfig {

    private final AfisPipelineProperties properties;

    public MinioConfig(AfisPipelineProperties properties) {
        this.properties = properties;
    }

    @Bean
    public MinioClient minioClient() {
        AfisPipelineProperties.Storage storage = properties.getStorage();
        return MinioClient.builder()
            .endpoint(storage.getEndpoint())
            .credentials(storage.getAccessKey(), storage.getSecretKey())
            .build();
    }

    @Bean
    public BlobStorageService blobStorageService(MinioClient minioClient) {
        BlobStorageService service = new BlobStorageService(minioClient);
        service.ensureBuckets(StorageBuckets.QUEUE_PROCESSING, StorageBuckets.STORE);
        return service;
    }
}
