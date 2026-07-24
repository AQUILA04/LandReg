package com.optimize.kopesa.afis.master.service.storage;

import com.optimize.common.blob.BlobStorageService;
import com.optimize.common.blob.StorageBuckets;
import org.springframework.stereotype.Service;

@Service
public class BlobArchivalService {

    private final BlobStorageService blobStorageService;

    public BlobArchivalService(BlobStorageService blobStorageService) {
        this.blobStorageService = blobStorageService;
    }

    public void promoteRid(String rid) {
        blobStorageService.movePrefix(StorageBuckets.QUEUE_PROCESSING, rid + "/", StorageBuckets.STORE, rid + "/");
    }

    public void deleteQueueRid(String rid) {
        blobStorageService.deletePrefix(StorageBuckets.QUEUE_PROCESSING, rid + "/");
    }
}
