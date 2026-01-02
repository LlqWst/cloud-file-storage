package dev.lqwd.cloudfilestorage.repository.storage.minio;

import dev.lqwd.cloudfilestorage.repository.storage.StorageBucket;
import io.minio.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
@Slf4j
@RequiredArgsConstructor
public class MinioBucketStorage implements StorageBucket {

    @Value("${app.bucket.name}")
    private String bucketName;

    private final MinioClient minioClient;
    private final MinioOperationTemplate operationTemplate;

    public void createBucketIfNotExists() {
        if (!isBucketExists()) {
            createRootBucket();
            log.info("Bucket '{}' created", bucketName);
        } else {
            log.debug("Bucket '{}' already exists", bucketName);
        }
    }

    public boolean isBucketExists() {
        return operationTemplate.execute(() ->
                        minioClient.bucketExists(
                                BucketExistsArgs.builder()
                                        .bucket(bucketName)
                                        .build()),
                "Error during checking for the existence of a bucket " + bucketName);
    }

    public void createRootBucket() {
        operationTemplate.execute(() ->
                    minioClient.makeBucket(
                            MakeBucketArgs.builder()
                                    .bucket(bucketName)
                                    .build()),
                "Error during creation of Minio bucket: " + bucketName);
    }

}