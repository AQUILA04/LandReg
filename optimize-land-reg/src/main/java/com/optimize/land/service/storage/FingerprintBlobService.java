package com.optimize.land.service.storage;

import com.optimize.common.blob.BlobStorageService;
import com.optimize.common.blob.ImageUri;
import com.optimize.common.blob.ImageUriResolver;
import com.optimize.common.blob.StorageBuckets;
import com.optimize.common.blob.kafka.AfisMasterRequestV2;
import com.optimize.common.blob.kafka.FingerRef;
import com.optimize.land.config.storage.MinioProperties;
import com.optimize.land.model.entity.FingerprintStore;
import com.optimize.land.model.enumeration.ActorType;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnBean(BlobStorageService.class)
@RequiredArgsConstructor
public class FingerprintBlobService {

    private final BlobStorageService blobStorageService;
    private final MinioProperties minioProperties;

    public void uploadFingerprints(String rid, Set<FingerprintStore> fingerprints) {
        for (FingerprintStore fingerprint : fingerprints) {
            uploadFingerprint(rid, fingerprint);
        }
    }

    public ImageUri uploadFingerprint(String rid, FingerprintStore fingerprint) {
        String fingerId = ImageUriResolver.fingerId(
            fingerprint.getHandType().name(),
            fingerprint.getFingerName().name()
        );
        String extension = ImageUriResolver.extensionFromContentType(fingerprint.getFingerprintImageContentType());
        ImageUri imageUri = ImageUriResolver.resolve(StorageBuckets.QUEUE_PROCESSING, rid, fingerId, extension);

        blobStorageService.upload(
            imageUri.bucket(),
            imageUri.objectKey(),
            fingerprint.getFingerprintImage(),
            fingerprint.getFingerprintImageContentType()
        );

        fingerprint.setImageUri(imageUri.uri());
        fingerprint.setImageBucket(imageUri.bucket());
        fingerprint.setImageObjectKey(imageUri.objectKey());
        if (minioProperties.isClaimCheckEnabled()) {
            fingerprint.setFingerprintImage(null);
        }
        return imageUri;
    }

    public AfisMasterRequestV2 toKafkaRequest(String rid, Set<FingerprintStore> fingerprints) {
        List<FingerRef> fingers = new ArrayList<>();
        for (FingerprintStore fingerprint : fingerprints) {
            String fingerId = ImageUriResolver.fingerId(
                fingerprint.getHandType().name(),
                fingerprint.getFingerName().name()
            );
            fingers.add(
                new FingerRef(
                    fingerId,
                    fingerprint.getHandType().name(),
                    fingerprint.getFingerName().name(),
                    fingerprint.getFingerprintImageContentType(),
                    fingerprint.getImageUri(),
                    fingerprint.getImageObjectKey(),
                    fingerprint.getImageBucket()
                )
            );
        }
        return new AfisMasterRequestV2(rid, ActorType.PHYSICAL_PERSON.name(), fingers);
    }

    public void promoteToStore(String rid) {
        blobStorageService.movePrefix(
            StorageBuckets.QUEUE_PROCESSING,
            rid + "/",
            StorageBuckets.STORE,
            rid + "/"
        );
    }

    public void deleteQueuePrefix(String rid) {
        blobStorageService.deletePrefix(StorageBuckets.QUEUE_PROCESSING, rid + "/");
    }

    public void updateFingerprintsToStoreBucket(Set<FingerprintStore> fingerprints) {
        for (FingerprintStore fingerprint : fingerprints) {
            if (fingerprint.getImageObjectKey() != null) {
                fingerprint.setImageBucket(StorageBuckets.STORE);
                fingerprint.setImageUri(ImageUri.of(StorageBuckets.STORE, fingerprint.getImageObjectKey()).uri());
            }
        }
    }
}
