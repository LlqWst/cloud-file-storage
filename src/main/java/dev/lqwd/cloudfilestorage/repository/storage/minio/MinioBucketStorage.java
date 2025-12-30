package dev.lqwd.cloudfilestorage.repository.storage.minio;

import dev.lqwd.cloudfilestorage.config.MinioConfiguration;
import dev.lqwd.cloudfilestorage.repository.storage.StorageBucket;
import io.minio.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;


@Component
@Slf4j
@RequiredArgsConstructor
public class MinioBucketStorage implements StorageBucket {

    private final MinioConfiguration minioConfig;
    private final MinioClient minioClient;
    private final MinioOperationTemplate operationTemplate;


    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        if (!isBucketExists()) {
            createRootBucket();
            log.info("Bucket '{}' created", minioConfig.getBucketName());
        } else {
            log.debug("Bucket '{}' already exists", minioConfig.getBucketName());
        }
    }

    private boolean isBucketExists() {
        return operationTemplate.execute(() ->
                        minioClient.bucketExists(
                                BucketExistsArgs.builder()
                                        .bucket(minioConfig.getBucketName())
                                        .build()),
                "Error during checking for the existence of a bucket" + minioConfig.getBucketName());
    }

    private void createRootBucket() {
        operationTemplate.execute(() ->
                    minioClient.makeBucket(
                            MakeBucketArgs.builder()
                                    .bucket(minioConfig.getBucketName())
                                    .build()),
                "Error during creation of Minio bucket: " + minioConfig.getBucketName());
    }

}