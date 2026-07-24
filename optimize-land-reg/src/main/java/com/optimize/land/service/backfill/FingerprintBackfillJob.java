package com.optimize.land.service.backfill;

import com.optimize.common.blob.BlobStorageService;
import com.optimize.common.blob.ImageUri;
import com.optimize.common.blob.ImageUriResolver;
import com.optimize.common.blob.StorageBuckets;
import com.optimize.land.config.storage.MinioProperties;
import com.optimize.land.model.entity.FingerprintStore;
import com.optimize.land.repository.FingerprintStoreRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@ConditionalOnBean(BlobStorageService.class)
@ConditionalOnProperty(prefix = "landreg.backfill", name = "enabled", havingValue = "true")
@RequiredArgsConstructor
@Slf4j
public class FingerprintBackfillJob {

    private final FingerprintStoreRepository fingerprintStoreRepository;
    private final BlobStorageService blobStorageService;
    private final MinioProperties minioProperties;

    @Scheduled(cron = "${landreg.backfill.cron:0 0 2 * * *}")
    @Transactional
    public void backfill() {
        List<FingerprintStore> pending = fingerprintStoreRepository.findTop100ByImageUriIsNullAndFingerprintImageIsNotNull();
        if (pending.isEmpty()) {
            return;
        }
        log.info("Backfilling {} fingerprint images to MinIO", pending.size());
        for (FingerprintStore fingerprint : pending) {
            try {
                String fingerId = ImageUriResolver.fingerId(
                    fingerprint.getHandType().name(),
                    fingerprint.getFingerName().name()
                );
                String extension = ImageUriResolver.extensionFromContentType(fingerprint.getFingerprintImageContentType());
                ImageUri imageUri = ImageUriResolver.resolve(StorageBuckets.STORE, fingerprint.getRid(), fingerId, extension);
                if (!blobStorageService.objectExists(imageUri.bucket(), imageUri.objectKey())) {
                    blobStorageService.upload(
                        imageUri.bucket(),
                        imageUri.objectKey(),
                        fingerprint.getFingerprintImage(),
                        fingerprint.getFingerprintImageContentType()
                    );
                }
                fingerprint.setImageUri(imageUri.uri());
                fingerprint.setImageBucket(imageUri.bucket());
                fingerprint.setImageObjectKey(imageUri.objectKey());
                if (minioProperties.isClaimCheckEnabled()) {
                    fingerprint.setFingerprintImage(null);
                }
                fingerprintStoreRepository.save(fingerprint);
            } catch (Exception e) {
                log.error("Backfill failed for fingerprint id={}: {}", fingerprint.getId(), e.getMessage());
            }
        }
    }
}
