package com.optimize.kopesa.afis.service.service.qdrant;

import static io.qdrant.client.PointIdFactory.id;
import static io.qdrant.client.ValueFactory.value;
import static io.qdrant.client.VectorsFactory.vectors;

import com.optimize.kopesa.afis.service.config.AfisPipelineProperties;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.grpc.Points.PointStruct;
import io.qdrant.client.grpc.Points.ScoredPoint;
import io.qdrant.client.grpc.Points.SearchPoints;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import org.springframework.stereotype.Service;

@Service
public class QdrantIndexService {

    private final QdrantClient qdrantClient;
    private final AfisPipelineProperties properties;

    public QdrantIndexService(QdrantClient qdrantClient, AfisPipelineProperties properties) {
        this.qdrantClient = qdrantClient;
        this.properties = properties;
    }

    public String upsert(String rid, String fingerId, float[] embedding, String imageUri) {
        String pointId = pointId(rid, fingerId).toString();
        List<Float> vector = new ArrayList<>(embedding.length);
        for (float v : embedding) {
            vector.add(v);
        }
        PointStruct point = PointStruct.newBuilder()
            .setId(id(UUID.fromString(pointId)))
            .setVectors(vectors(vector))
            .putAllPayload(
                Map.of(
                    "rid",
                    value(rid),
                    "finger_id",
                    value(fingerId),
                    "image_uri",
                    value(imageUri),
                    "actor_type",
                    value("PERSON")
                )
            )
            .build();
        try {
            qdrantClient
                .upsertAsync(properties.getQdrant().getCollection(), List.of(point))
                .get();
            return pointId;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Qdrant upsert interrupted", e);
        } catch (ExecutionException e) {
            throw new IllegalStateException("Qdrant upsert failed", e);
        }
    }

    public List<ScoredPoint> search(float[] embedding, int limit) {
        List<Float> vector = new ArrayList<>(embedding.length);
        for (float v : embedding) {
            vector.add(v);
        }
        SearchPoints search = SearchPoints.newBuilder()
            .setCollectionName(properties.getQdrant().getCollection())
            .addAllVector(vector)
            .setLimit(limit)
            .setWithPayload(io.qdrant.client.grpc.Points.WithPayloadSelector.newBuilder().setEnable(true).build())
            .build();
        try {
            return qdrantClient.searchAsync(search).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Qdrant search interrupted", e);
        } catch (ExecutionException e) {
            throw new IllegalStateException("Qdrant search failed", e);
        }
    }

    public static UUID pointId(String rid, String fingerId) {
        return UUID.nameUUIDFromBytes((rid + ":" + fingerId).getBytes());
    }
}
