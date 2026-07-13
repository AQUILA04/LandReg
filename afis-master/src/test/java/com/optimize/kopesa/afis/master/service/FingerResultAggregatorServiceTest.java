package com.optimize.kopesa.afis.master.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.optimize.common.blob.StorageBuckets;
import com.optimize.kopesa.afis.master.broker.MasterFeedbackProducer;
import com.optimize.kopesa.afis.master.domain.FingerprintStore;
import com.optimize.kopesa.afis.master.domain.ProcessingFingerprint;
import com.optimize.kopesa.afis.master.repository.FingerprintStoreRepository;
import com.optimize.kopesa.afis.master.repository.ProcessingFingerprintRepository;
import com.optimize.kopesa.afis.master.service.storage.BlobArchivalService;
import java.lang.reflect.Method;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FingerResultAggregatorServiceTest {

    @Mock
    private MatcherJobHistoryService matcherJobHistoryService;
    @Mock
    private ProcessingFingerprintRepository processingFingerprintRepository;
    @Mock
    private FingerprintStoreRepository fingerprintStoreRepository;
    @Mock
    private BlobArchivalService blobArchivalService;
    @Mock
    private MasterFeedbackProducer feedbackProducer;

    private FingerResultAggregatorService service;

    @BeforeEach
    void setUp() {
        service = new FingerResultAggregatorService(
            matcherJobHistoryService,
            processingFingerprintRepository,
            fingerprintStoreRepository,
            blobArchivalService,
            feedbackProducer
        );
    }

    @Test
    void toFingerprintStore_usesObjectKeyWhenPresent() throws Exception {
        ProcessingFingerprint processing = baseProcessing();
        processing.setImageObjectKey("RID-1/LEFT_THUMB.jpg");

        FingerprintStore store = invokeToFingerprintStore(processing);

        assertEquals("RID-1/LEFT_THUMB.jpg", store.getImageObjectKey());
        assertEquals(StorageBuckets.STORE, store.getImageBucket());
        assertEquals("s3://store/RID-1/LEFT_THUMB.jpg", store.getImageUri());
    }

    @Test
    void toFingerprintStore_derivesObjectKeyFromUriWhenKeyMissing() throws Exception {
        ProcessingFingerprint processing = baseProcessing();
        processing.setImageUri("s3://queue-processing/RID-1/LEFT_THUMB.jpg");
        processing.setImageBucket(StorageBuckets.QUEUE_PROCESSING);

        FingerprintStore store = invokeToFingerprintStore(processing);

        assertEquals("RID-1/LEFT_THUMB.jpg", store.getImageObjectKey());
        assertEquals(StorageBuckets.STORE, store.getImageBucket());
        assertEquals("s3://store/RID-1/LEFT_THUMB.jpg", store.getImageUri());
    }

    @Test
    void toFingerprintStore_derivesObjectKeyFromRidAndFingerIdWhenNoBlobReference() throws Exception {
        ProcessingFingerprint processing = baseProcessing();
        processing.setFingerprintImageContentType("image/jpeg");

        FingerprintStore store = invokeToFingerprintStore(processing);

        assertEquals("RID-1/LEFT_THUMB.jpg", store.getImageObjectKey());
        assertEquals(StorageBuckets.STORE, store.getImageBucket());
        assertEquals("s3://store/RID-1/LEFT_THUMB.jpg", store.getImageUri());
    }

    @Test
    void toFingerprintStore_keepsLegacyInlineImageWhenNoBlobReference() throws Exception {
        ProcessingFingerprint processing = new ProcessingFingerprint();
        processing.setFingerprintImage(new byte[] { 1, 2, 3 });

        FingerprintStore store = invokeToFingerprintStore(processing);

        assertNull(store.getImageObjectKey());
        assertNull(store.getImageUri());
        assertNull(store.getImageBucket());
        assertEquals(3, store.getFingerprintImage().length);
    }

    private static ProcessingFingerprint baseProcessing() {
        ProcessingFingerprint processing = new ProcessingFingerprint();
        processing.setRid("RID-1");
        processing.setFingerId("LEFT_THUMB");
        return processing;
    }

    private FingerprintStore invokeToFingerprintStore(ProcessingFingerprint processing) throws Exception {
        Method method = FingerResultAggregatorService.class.getDeclaredMethod("toFingerprintStore", ProcessingFingerprint.class);
        method.setAccessible(true);
        return (FingerprintStore) method.invoke(service, processing);
    }
}
