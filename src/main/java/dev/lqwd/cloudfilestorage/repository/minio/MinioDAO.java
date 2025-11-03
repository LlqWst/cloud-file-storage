package dev.lqwd.cloudfilestorage.repository.minio;

import dev.lqwd.cloudfilestorage.exception.NotFoundException;
import dev.lqwd.cloudfilestorage.utils.PathNormalizer;
import dev.lqwd.cloudfilestorage.utils.path_processor.ProcessedPath;
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

    @Value("${app.minio.bucket.name}")
    private String bucketName;

    private final MinioClient minioClient;
    private final MinioOperationTemplate operationTemplate;
    private final PathNormalizer pathNormalizer;


    public boolean isExist(ProcessedPath path) {
        return findResource(path.requestedPath(), path.parentPath()).isPresent();
    }

    public Optional<Item> findResource(String fullPath, String parentPath) {
        String pathWithoutEndSlash = pathNormalizer.getPathWithoutEndSlash(fullPath);
        Iterable<Result<Item>> results = getItems(parentPath);
        return operationTemplate.execute(() -> {
                    for (Result<Item> item : results) {
                        String itemPath = item.get().objectName();
                        if (itemPath.equals(fullPath)) {
                            return Optional.of(item.get());
                        }
                        if (itemPath.equals(pathWithoutEndSlash)) {
                            return Optional.empty();
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
                        items.add(item.get());
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

    private void removeResource(String resourceName) {
        operationTemplate.execute(() ->
                        minioClient.removeObject(
                                RemoveObjectArgs.builder()
                                        .bucket(bucketName)
                                        .object(resourceName)
                                        .build()),
                "Minio error during deletion of resource: " + resourceName,
                "Unexpected error during deletion of resource: " + resourceName);
    }

    private boolean isDirExist(String path) {
        try {
            return findResource(path, path).isPresent();
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
