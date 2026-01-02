package dev.lqwd.cloudfilestorage.repository.storage.minio;

import dev.lqwd.cloudfilestorage.repository.storage.FindResourceStorage;
import io.minio.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;


@Component
@RequiredArgsConstructor
public class MinioFindStorage implements FindResourceStorage<StatObjectResponse> {

    @Value("${app.bucket.name}")
    private String bucketName;

    private final MinioClient minioClient;
    private final MinioOperationTemplate operationTemplate;


    public Optional<StatObjectResponse> findResource(String pathWithUserDir) {
        return operationTemplate.findResource(() ->
                        minioClient.statObject(
                                StatObjectArgs.builder()
                                        .bucket(bucketName)
                                        .object(pathWithUserDir)
                                        .build()),
                "Error during finding resource: " + pathWithUserDir);
    }

}