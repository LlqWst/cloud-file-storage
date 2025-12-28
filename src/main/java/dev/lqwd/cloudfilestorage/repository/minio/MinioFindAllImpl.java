package dev.lqwd.cloudfilestorage.repository.minio;

import dev.lqwd.cloudfilestorage.exception.InternalErrorException;
import dev.lqwd.cloudfilestorage.repository.FindAllResourcesStorageDao;
import io.minio.*;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


import java.util.List;
import java.util.stream.StreamSupport;


@Component
@Slf4j
@RequiredArgsConstructor
public class MinioFindAllImpl implements FindAllResourcesStorageDao<Item> {

    @Value("${app.minio.bucket.name}")
    private String bucketName;

    private final MinioClient minioClient;
    private final MinioOperationTemplate operationTemplate;

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
                        .bucket(bucketName)
                        .prefix(pathWithUserDir)
                        .recursive(isRecursive)
                        .build());
    }

    private Item safeGetItem(Result<Item> result) {
        try {
            return result.get();
        } catch (Exception e) {
            throw new InternalErrorException("Failed to get item from MinIO result " + result, e);
        }
    }

}