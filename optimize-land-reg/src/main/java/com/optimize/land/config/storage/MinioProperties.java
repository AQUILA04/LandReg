package com.optimize.land.config.storage;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "landreg.storage")
public class MinioProperties {

    private boolean enabled = false;
    private boolean claimCheckEnabled = false;
    private String endpoint = "http://localhost:9000";
    private String accessKey = "minioadmin";
    private String secretKey = "minioadmin";
    private String queueBucket = "queue-processing";
    private String storeBucket = "store";
}
