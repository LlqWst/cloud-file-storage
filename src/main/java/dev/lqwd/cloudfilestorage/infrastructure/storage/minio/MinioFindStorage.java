package dev.lqwd.cloudfilestorage.infrastructure.storage.minio;

import dev.lqwd.cloudfilestorage.dto.property.StorageProperties;
import dev.lqwd.cloudfilestorage.infrastructure.storage.FindStorage;
import io.minio.MinioClient;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;


@Component
@RequiredArgsConstructor
public class MinioFindStorage implements FindStorage<StatObjectResponse> {

    private final StorageProperties storageProperties;
    private final MinioClient minioClient;
    private final MinioOperationTemplate operationTemplate;

    @Override
    public Optional<StatObjectResponse> findResource(String pathWithUserDir) {
        return operationTemplate.findResource(() ->
                        minioClient.statObject(
                                StatObjectArgs.builder()
                                        .bucket(storageProperties.bucketName())
                                        .object(pathWithUserDir)
                                        .build()),
                "Error during finding resource: " + pathWithUserDir);
    }

}