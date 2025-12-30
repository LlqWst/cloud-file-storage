package dev.lqwd.cloudfilestorage.repository.storage.minio;

import dev.lqwd.cloudfilestorage.config.MinioConfiguration;
import dev.lqwd.cloudfilestorage.repository.storage.FindResourceStorage;
import io.minio.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;


@Component
@RequiredArgsConstructor
public class MinioFindStorage implements FindResourceStorage<StatObjectResponse> {

    private final MinioConfiguration minioConfig;
    private final MinioClient minioClient;
    private final MinioOperationTemplate operationTemplate;


    public Optional<StatObjectResponse> findResource(String pathWithUserDir) {
        return operationTemplate.findResource(() ->
                        minioClient.statObject(
                                StatObjectArgs.builder()
                                        .bucket(minioConfig.getBucketName())
                                        .object(pathWithUserDir)
                                        .build()),
                "Error during finding resource: " + pathWithUserDir);
    }

}