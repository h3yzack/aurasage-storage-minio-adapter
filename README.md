# AuraSage Storage MinIO Adapter

MinIO storage implementation adapter for the AuraSage Storage Service using AWS SDK S3 client.

## Features

- Presigned URL generation for file uploads and downloads
- File deletion operations
- S3-compatible API using MinIO server
- Auto-configuration for Spring Boot applications
- Default configuration with environment variable overrides

## Usage

Add dependency to your project:

```gradle
dependencies {
    implementation 'io.aurasage:aurasage-storage-minio-adapter:0.0.1-SNAPSHOT'
}
```

## Integration

This adapter is used by the AuraSage Storage Service to provide MinIO storage backend functionality. The adapter automatically configures a `StorageService` bean that implements:

- Upload URL generation
- Download URL generation  
- File deletion operations

## Configuration

The adapter provides default values that can be overridden:

```properties
# MinIO server configuration
aurasage.storage.minio.url=${MINIO_URL:http://localhost:9000}
aurasage.storage.minio.access-key=${MINIO_ACCESS_KEY:aurasage}
aurasage.storage.minio.secret-key=${MINIO_SECRET_KEY:devpassword}
aurasage.storage.minio.bucket-name=${BUCKET_NAME:aurasage}
```

## Components

- **MinioStorageService** - Implementation of StorageService interface
- **MinioStorageAutoConfiguration** - Auto-configuration for Spring Boot
- **MinioProperties** - Configuration properties binding
- **S3Client/S3Presigner** - AWS SDK clients for MinIO operations

## Environment Variables

- `MINIO_URL` - MinIO server endpoint (default: http://localhost:9000)
- `MINIO_ACCESS_KEY` - MinIO access key (default: aurasage)
- `MINIO_SECRET_KEY` - MinIO secret key (default: devpassword)
- `BUCKET_NAME` - Storage bucket name (default: aurasage)