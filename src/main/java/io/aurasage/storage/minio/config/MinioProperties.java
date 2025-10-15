package io.aurasage.storage.minio.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "aurasage.storage.minio")
@Getter
@Setter
public class MinioProperties {

    private String url;
    private String publicUrl;
    private String accessKey;
    private String secretKey;
    private String bucketName;
    
}
