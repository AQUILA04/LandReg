package com.optimize.common.blob;

public final class ImageUriResolver {

    private ImageUriResolver() {}

    public static ImageUri resolve(String bucket, String rid, String fingerId, String extension) {
        String normalizedExtension = extension.startsWith(".") ? extension : "." + extension;
        String objectKey = rid + "/" + fingerId + normalizedExtension;
        return ImageUri.of(bucket, objectKey);
    }

    public static String fingerId(String handType, String fingerName) {
        return handType + "_" + fingerName;
    }

    public static String extensionFromContentType(String contentType) {
        if (contentType == null) {
            return ".jpg";
        }
        return switch (contentType.toLowerCase()) {
            case "image/png" -> ".png";
            case "image/bmp", "image/x-ms-bmp" -> ".bmp";
            case "image/wsq" -> ".wsq";
            default -> ".jpg";
        };
    }

    public static ParsedUri parse(String uri) {
        if (uri == null || uri.isBlank()) {
            throw new IllegalArgumentException("URI must not be blank");
        }
        if (uri.startsWith("s3://")) {
            String withoutScheme = uri.substring(5);
            int slash = withoutScheme.indexOf('/');
            if (slash < 1) {
                throw new IllegalArgumentException("Invalid S3 URI: " + uri);
            }
            return new ParsedUri(withoutScheme.substring(0, slash), withoutScheme.substring(slash + 1));
        }
        int slash = uri.indexOf('/');
        if (slash < 1) {
            throw new IllegalArgumentException("Invalid bucket/key URI: " + uri);
        }
        return new ParsedUri(uri.substring(0, slash), uri.substring(slash + 1));
    }

    public record ParsedUri(String bucket, String objectKey) {}
}
