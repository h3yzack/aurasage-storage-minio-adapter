package io.aurasage.storage.minio.service;

import java.time.Duration;

import io.aurasage.core.storage.service.StorageService;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectResponse;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Slf4j
public class MinioStorageService implements StorageService {

    private final S3Client minioS3Client;
    private final S3Presigner minioS3Presigner;
    private final String bucketName;

    public MinioStorageService(S3Client minioS3Client, S3Presigner minioS3Presigner, String bucketName) {
        this.minioS3Client = minioS3Client;
        this.minioS3Presigner = minioS3Presigner;
        this.bucketName = bucketName;
    }


    @Override
    public String generatePresignedUploadUrl(String userId, String objectKey, String fileName, int expiryInSeconds) {

        log.info("Generating presigned URL for upload: {}/{}", bucketName, objectKey);

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofSeconds(expiryInSeconds))
                .putObjectRequest(por -> por
                                .bucket(bucketName)
                                .key(objectKey))
                .build();

        return minioS3Presigner.presignPutObject(presignRequest).url().toString();
    }

    @Override
    public String generatePresignedDownloadUrl(String objectKey, int expiryInSeconds) {

        log.info("Generating presigned URL for download: {}/{}", bucketName, objectKey);

        // Check if object exists first
        try {
            minioS3Client.headObject(r -> r.bucket(bucketName).key(objectKey));
            log.debug("Object exists: {}/{}", bucketName, objectKey);
        } catch (Exception e) {
            log.error("Object not found: {}/{}", bucketName, objectKey);
            throw new RuntimeException("Object not found: " + objectKey, e);
        }
        
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofSeconds(expiryInSeconds))
                .getObjectRequest(r -> r.bucket(bucketName).key(objectKey))
                .build();

        return minioS3Presigner.presignGetObject(presignRequest).url().toString();
    }

    @Override
    public void deleteObject(String objectKey) {

        log.info("Deleting object: {}/{}", bucketName, objectKey);
        try {
            DeleteObjectResponse response = minioS3Client.deleteObject(r -> r.bucket(bucketName).key(objectKey));

            log.info("Delete result: {}", response.toString());
        } catch (Exception e) {
            log.error("Error deleting object {}/{}: {}", bucketName, objectKey, e.getMessage());

            throw new RuntimeException("Minio operation (deleteObject) failed", e);
        }
    }

    

}
