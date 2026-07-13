package com.optimize.common.blob;

import io.minio.BucketExistsArgs;
import io.minio.CopyObjectArgs;
import io.minio.CopySource;
import io.minio.GetObjectArgs;
import io.minio.ListObjectsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.Result;
import io.minio.messages.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class BlobStorageService {

    private static final Logger log = LoggerFactory.getLogger(BlobStorageService.class);

    private final MinioClient minioClient;

    public BlobStorageService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    public void ensureBuckets(String... buckets) {
        for (String bucket : buckets) {
            try {
                boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
                if (!exists) {
                    minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
                    log.info("Created MinIO bucket: {}", bucket);
                }
            } catch (Exception e) {
                throw new BlobStorageException("Failed to ensure bucket: " + bucket, e);
            }
        }
    }

    public ImageUri upload(String bucket, String objectKey, byte[] data, String contentType) {
        try (InputStream stream = new ByteArrayInputStream(data)) {
            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectKey)
                    .stream(stream, data.length, -1)
                    .contentType(contentType != null ? contentType : "application/octet-stream")
                    .build()
            );
            return ImageUri.of(bucket, objectKey);
        } catch (Exception e) {
            throw new BlobStorageException("Upload failed for " + bucket + "/" + objectKey, e);
        }
    }

    public byte[] download(String bucket, String objectKey) {
        try (InputStream stream = minioClient.getObject(
            GetObjectArgs.builder().bucket(bucket).object(objectKey).build()
        )) {
            return stream.readAllBytes();
        } catch (Exception e) {
            throw new BlobStorageException("Download failed for " + bucket + "/" + objectKey, e);
        }
    }

    public byte[] download(ImageUri imageUri) {
        return download(imageUri.bucket(), imageUri.objectKey());
    }

    public byte[] downloadUri(String uri) {
        ImageUriResolver.ParsedUri parsed = ImageUriResolver.parse(uri);
        return download(parsed.bucket(), parsed.objectKey());
    }

    public void copyObject(String sourceBucket, String sourceKey, String destBucket, String destKey) {
        try {
            minioClient.copyObject(
                CopyObjectArgs.builder()
                    .bucket(destBucket)
                    .object(destKey)
                    .source(CopySource.builder().bucket(sourceBucket).object(sourceKey).build())
                    .build()
            );
        } catch (Exception e) {
            throw new BlobStorageException(
                "Copy failed from " + sourceBucket + "/" + sourceKey + " to " + destBucket + "/" + destKey,
                e
            );
        }
    }

    public void movePrefix(String sourceBucket, String sourcePrefix, String destBucket, String destPrefix) {
        for (String objectKey : listObjectKeys(sourceBucket, sourcePrefix)) {
            String relative = objectKey.substring(sourcePrefix.length());
            if (relative.startsWith("/")) {
                relative = relative.substring(1);
            }
            String destKey = destPrefix.endsWith("/") ? destPrefix + relative : destPrefix + "/" + relative;
            copyObject(sourceBucket, objectKey, destBucket, destKey);
            deleteObject(sourceBucket, objectKey);
        }
    }

    public void deletePrefix(String bucket, String prefix) {
        for (String objectKey : listObjectKeys(bucket, prefix)) {
            deleteObject(bucket, objectKey);
        }
    }

    public void deleteObject(String bucket, String objectKey) {
        try {
            minioClient.removeObject(
                RemoveObjectArgs.builder().bucket(bucket).object(objectKey).build()
            );
        } catch (Exception e) {
            throw new BlobStorageException("Delete failed for " + bucket + "/" + objectKey, e);
        }
    }

    public boolean objectExists(String bucket, String objectKey) {
        try (InputStream ignored = minioClient.getObject(
            GetObjectArgs.builder().bucket(bucket).object(objectKey).build()
        )) {
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public List<String> listObjectKeys(String bucket, String prefix) {
        List<String> keys = new ArrayList<>();
        try {
            Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder().bucket(bucket).prefix(prefix).recursive(true).build()
            );
            for (Result<Item> result : results) {
                Item item = result.get();
                if (!item.isDir()) {
                    keys.add(item.objectName());
                }
            }
        } catch (Exception e) {
            throw new BlobStorageException("List failed for " + bucket + "/" + prefix, e);
        }
        return keys;
    }
}
