package dev.lqwd.cloudfilestorage.infrastructure.storage.minio;

import dev.lqwd.cloudfilestorage.dto.property.StorageProperties;
import dev.lqwd.cloudfilestorage.exception.StorageException;
import dev.lqwd.cloudfilestorage.infrastructure.storage.FindAllStorage;
import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.Result;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;


@Component
@RequiredArgsConstructor
public class MinioFindAllStorage implements FindAllStorage<Item> {

    private final StorageProperties storageProperties;
    private final MinioClient minioClient;
    private final MinioOperationTemplate operationTemplate;

    @Override
    public List<Item> findResources(String pathWithUserDir, boolean isRecursive) {
       return findItems(pathWithUserDir, isRecursive).toList();
    }

    @Override
    public List<String> findAllResourcePaths(String pathWithUserDir, boolean isRecursive) {
        return findItems(pathWithUserDir, isRecursive)
                .map(Item::objectName)
                .toList();
    }

    private Stream<Item> findItems(String pathWithUserDir, boolean isRecursive) {
        return operationTemplate.execute(() ->
                        StreamSupport.stream(
                                        getResultItems(pathWithUserDir, isRecursive)
                                                .spliterator(), false)
                                .map(this::safeGetItem),
                "Error during getting of directory's resources. path: " + pathWithUserDir);
    }

    private Iterable<Result<Item>> getResultItems(String pathWithUserDir, boolean isRecursive) {
        return minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(storageProperties.bucketName())
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