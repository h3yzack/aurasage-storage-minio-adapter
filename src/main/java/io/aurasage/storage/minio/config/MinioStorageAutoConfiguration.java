package io.aurasage.storage.minio.config;

import java.net.URI;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import io.aurasage.storage.minio.service.MinioStorageService;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@AutoConfiguration
@ConditionalOnClass({S3Client.class, S3Presigner.class})
@EnableConfigurationProperties(MinioProperties.class)
public class MinioStorageAutoConfiguration {

    /**
     * Configures the S3Client to connect to the Minio server endpoint.
     */
    @Bean(name = "minioS3Client")
    @ConditionalOnMissingBean
    public S3Client minioS3Client(MinioProperties minioProperties) {

        return S3Client.builder()
                .endpointOverride(URI.create(minioProperties.getUrl()))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(minioProperties.getAccessKey(), minioProperties.getSecretKey())))
                .region(Region.US_EAST_1)
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true)
                        .build())
                .build();
    }

    /**
     * Configures the S3Presigner using the Minio client's settings.
     */
    @Bean(name = "minioS3Presigner")
    @ConditionalOnMissingBean
    public S3Presigner minioS3Presigner(MinioProperties minioProperties) {
        return S3Presigner.builder()
                .endpointOverride(URI.create(minioProperties.getPublicUrl()))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(minioProperties.getAccessKey(), minioProperties.getSecretKey())))
                .region(Region.US_EAST_1)
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true)
                        .build())
                .build();
    }

    @Bean
    @ConditionalOnMissingBean
    public MinioStorageService minioStorageService(S3Client minioS3Client, S3Presigner minioS3Presigner, MinioProperties minioProperties) {
        return new MinioStorageService(minioS3Client, minioS3Presigner, minioProperties.getBucketName());
    }
}
