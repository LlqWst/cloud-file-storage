package dev.lqwd.cloudfilestorage.repository.minio;

import io.minio.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;


@Component
@Slf4j
@RequiredArgsConstructor
public class MinioBucketDAO {

    @Value("${app.minio.bucket.name}")
    private String bucketName;

    private final MinioClient minioClient;
    private final MinioOperationTemplate operationTemplate;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        if (!isBucketExists()) {
            createRootBucket();
        }
    }

    private boolean isBucketExists() {
        return operationTemplate.execute(() ->
                        minioClient.bucketExists(
                                BucketExistsArgs.builder()
                                        .bucket(bucketName)
                                        .build()),
                "Minio error during checking for the existence of a bucket" + bucketName,
                "Unexpected error during checking for the existence of a bucket" + bucketName);
    }

    private void createRootBucket() {
        operationTemplate.execute(() -> {
                    minioClient.makeBucket(
                            MakeBucketArgs.builder()
                                    .bucket(bucketName)
                                    .build());
                    log.info("Bucket '{}' created", bucketName);
                },
                "Minio error during creation of Minio bucket: " + bucketName,
                "Unexpected error during creation of Minio bucket: " + bucketName);
    }

}
