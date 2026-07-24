package com.optimize.kopesa.afis.service.config.qdrant;

import com.optimize.kopesa.afis.service.config.AfisPipelineProperties;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.QdrantGrpcClient;
import io.qdrant.client.grpc.Collections.Distance;
import io.qdrant.client.grpc.Collections.VectorParams;
import java.util.concurrent.ExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QdrantConfig {

    private static final Logger log = LoggerFactory.getLogger(QdrantConfig.class);
    private final AfisPipelineProperties properties;

    public QdrantConfig(AfisPipelineProperties properties) {
        this.properties = properties;
    }

    @Bean(destroyMethod = "close")
    public QdrantClient qdrantClient() throws ExecutionException, InterruptedException {
        AfisPipelineProperties.Qdrant qdrant = properties.getQdrant();
        QdrantClient client = new QdrantClient(
            QdrantGrpcClient.newBuilder(qdrant.getHost(), qdrant.getGrpcPort(), false).build()
        );
        ensureCollection(client, qdrant.getCollection(), qdrant.getVectorSize());
        return client;
    }

    private void ensureCollection(QdrantClient client, String collectionName, int vectorSize)
        throws ExecutionException, InterruptedException {
        boolean exists = client.collectionExistsAsync(collectionName).get();
        if (!exists) {
            client.createCollectionAsync(
                collectionName,
                VectorParams.newBuilder().setSize(vectorSize).setDistance(Distance.Cosine).build()
            ).get();
            log.info("Created Qdrant collection {}", collectionName);
        }
    }
}
