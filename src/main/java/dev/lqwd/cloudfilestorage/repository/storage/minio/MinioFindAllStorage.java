package dev.lqwd.cloudfilestorage.repository.storage.minio;

import dev.lqwd.cloudfilestorage.config.MinioConfiguration;
import dev.lqwd.cloudfilestorage.exception.StorageException;
import dev.lqwd.cloudfilestorage.repository.storage.FindAllResourcesStorage;
import io.minio.*;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.StreamSupport;


@Component
@RequiredArgsConstructor
public class MinioFindAllStorage implements FindAllResourcesStorage<Item> {

    private final MinioClient minioClient;
    private final MinioOperationTemplate operationTemplate;
    private final MinioConfiguration minioConfig;


    public List<Item> findResources(String pathWithUserDir, boolean isRecursive) {
        return operationTemplate.execute(() ->
                        StreamSupport.stream(
                                        getResultItems(pathWithUserDir, isRecursive)
                                                .spliterator(), false)
                                .map(this::safeGetItem)
                                .toList(),
                "Error during getting of directory's resources. path: " + pathWithUserDir);
    }

    private Iterable<Result<Item>> getResultItems(String pathWithUserDir, boolean isRecursive) {
        return minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(minioConfig.getBucketName())
                        .prefix(pathWithUserDir)
                        .recursive(isRecursive)
                        .build());
    }

    private Item safeGetItem(Result<Item> result) {
        try {
            return result.get();
        } catch (Exception e) {
            throw new StorageException("Failed to get item from MinIO result " + result, e);
        }
    }

}