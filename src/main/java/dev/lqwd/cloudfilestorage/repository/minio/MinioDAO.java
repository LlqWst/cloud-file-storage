package dev.lqwd.cloudfilestorage.repository.minio;

import dev.lqwd.cloudfilestorage.exception.NotFoundException;
import dev.lqwd.cloudfilestorage.utils.PathNormalizer;
import io.minio.*;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Component
@Slf4j
@RequiredArgsConstructor
public class MinioDAO {

    public static final String SLASH = "/";
    @Value("${app.minio.bucket.name}")
    private String bucketName;

    private final MinioClient minioClient;
    private final MinioOperationTemplate operationTemplate;
    private final PathNormalizer pathNormalizer;


    public Optional<Item> findResource(String fullPath, String parentPath) {
        Optional<Item> item = findResourceIgnoreEndSlash(fullPath, parentPath);
        if (item.isPresent() && item.get().objectName().equals(fullPath)) {
            return item;
        }
        return Optional.empty();
    }

    private Optional<Item> findResourceIgnoreEndSlash(String fullPath, String parentPath) {
        String pathWithoutEndSlash = pathNormalizer.getPathWithoutEndSlash(fullPath);
        String pathWithEndSlash = pathNormalizer.getPathWithoutEndSlash(fullPath) + SLASH;
        Iterable<Result<Item>> results = getItems(parentPath);
        return operationTemplate.execute(() -> {
                    for (Result<Item> item : results) {
                        String itemPath = item.get().objectName();
                        if (itemPath.equals(pathWithoutEndSlash) ||
                            itemPath.equals(pathWithEndSlash)) {
                            return Optional.of(item.get());
                        }
                    }
                    return Optional.empty();
                },
                "Minio error during getting of resource: " + fullPath,
                "Unexpected error during getting of resource: " + fullPath);
    }

    public List<Item> findDirectoryResources(String fullPath) {
        Iterable<Result<Item>> results = getItems(fullPath);
        return operationTemplate.execute(() -> {
                    List<Item> items = new ArrayList<>();
                    for (Result<Item> item : results) {
                        if (!item.get().objectName().equals(fullPath)) {
                            items.add(item.get());
                        }
                    }
                    return items;
                },
                "Minio error during getting of directory's resources: " + fullPath,
                "Unexpected error during getting of directory's resources " + fullPath);
    }

    public void createDirectory(String path) {
        operationTemplate.execute(() ->
                        minioClient.putObject(
                                PutObjectArgs.builder()
                                        .bucket(bucketName)
                                        .object(path)
                                        .stream(new ByteArrayInputStream(new byte[]{}), 0, -1)
                                        .build()),
                "Minio error during creation of directory: " + path,
                "Unexpected error during creation of directory: " + path);
    }

    public void buildFile(String fullPath) {
        StringBuilder builder = new StringBuilder();
        builder.append("some text for test");
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(
                    builder.toString().getBytes(StandardCharsets.UTF_8));

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fullPath)
                            .stream(bais, bais.available(), -1)
                            .build());
            bais.close();
            log.info("file create for: {}", fullPath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void removeResource(String resourceName) {
        operationTemplate.execute(() ->
                        minioClient.removeObject(
                                RemoveObjectArgs.builder()
                                        .bucket(bucketName)
                                        .object(resourceName)
                                        .build()),
                "Minio error during deletion of resource: " + resourceName,
                "Unexpected error during deletion of resource: " + resourceName);
    }

    public boolean isExist(String fullPath, String parentPath) {
        return findResourceIgnoreEndSlash(fullPath, parentPath)
                .isPresent();
    }

    public boolean isDirExist(String path) {
        try {
            return isExist(path, path);
        } catch (NotFoundException _) {
            return false;
        }
    }

    @NotNull
    private Iterable<Result<Item>> getItems(String parentPath) {
        Iterable<Result<Item>> results =
                minioClient.listObjects(
                        ListObjectsArgs.builder()
                                .bucket(bucketName)
                                .prefix(parentPath)
                                .build());
        if (!results.iterator().hasNext()) {
            throw new NotFoundException("Directory doesn't exist: " + parentPath);
        }
        return results;
    }

}