package com.optimize.common.blob;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ImageUriResolverTest {

    @Test
    void buildsS3Uri() {
        ImageUri uri = ImageUriResolver.resolve("queue-processing", "RID-1", "LEFT_THUMB", ".jpg");
        assertEquals("queue-processing", uri.bucket());
        assertEquals("RID-1/LEFT_THUMB.jpg", uri.objectKey());
        assertEquals("s3://queue-processing/RID-1/LEFT_THUMB.jpg", uri.uri());
    }

    @Test
    void parsesS3Uri() {
        ImageUriResolver.ParsedUri parsed = ImageUriResolver.parse("s3://store/RID-1/LEFT_THUMB.jpg");
        assertEquals("store", parsed.bucket());
        assertEquals("RID-1/LEFT_THUMB.jpg", parsed.objectKey());
    }
}
