package com.optimize.common.blob;

public record ImageUri(String bucket, String objectKey, String uri) {

    public static ImageUri of(String bucket, String objectKey) {
        return new ImageUri(bucket, objectKey, "s3://" + bucket + "/" + objectKey);
    }
}
