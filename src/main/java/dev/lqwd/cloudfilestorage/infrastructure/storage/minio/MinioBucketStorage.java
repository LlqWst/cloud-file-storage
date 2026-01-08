package dev.lqwd.cloudfilestorage.infrastructure.storage.minio;

import dev.lqwd.cloudfilestorage.dto.property.StorageProperties;
import dev.lqwd.cloudfilestorage.infrastructure.storage.BucketStorage;
import io.minio.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Component
@Slf4j
@RequiredArgsConstructor
public class MinioBucketStorage implements BucketStorage {

    private final StorageProperties storageProperties;
    private final MinioClient minioClient;
    private final MinioOperationTemplate operationTemplate;

    @Override
    public void createBucketIfNotExists() {
        if (!isBucketExists()) {
            createRootBucket();
            log.info("Bucket '{}' created", storageProperties.bucketName());
        } else {
            log.debug("Bucket '{}' already exists", storageProperties.bucketName());
        }
    }

    @Override
    public boolean isBucketExists() {
        return operationTemplate.execute(() ->
                        minioClient.bucketExists(
                                BucketExistsArgs.builder()
                                        .bucket(storageProperties.bucketName())
                                        .build()),
                "Error during checking for the existence of a bucket " + storageProperties.bucketName());
    }

    @Override
    public void createRootBucket() {
        operationTemplate.execute(() ->
                    minioClient.makeBucket(
                            MakeBucketArgs.builder()
                                    .bucket(storageProperties.bucketName())
                                    .build()),
                "Error during creation of Minio bucket: " + storageProperties.bucketName());
    }

}