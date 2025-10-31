package dev.lqwd.cloudfilestorage.service;

import dev.lqwd.cloudfilestorage.exception.AlreadyExistException;
import dev.lqwd.cloudfilestorage.exception.NotFoundException;
import dev.lqwd.cloudfilestorage.model.ProcessedPath;
import io.minio.*;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;


@Service
@Slf4j
@RequiredArgsConstructor
public class MinioService {

    private static final String SLASH = "/";

    @Value("${app.minio.bucket.name}")
    private String bucketName;

    @Value("${app.minio.root.template.name}")
    private String userRootTemplate;

    private final MinioClient minioClient;
    private final MinioOperationTemplate operationTemplate;

    public void createUserRootDir(Long id) {
        String pathWithoutSlash = getUserRootDir(id);
        if (!isResourceExist(pathWithoutSlash)) {
            createDirectory(pathWithoutSlash);
        }
    }

    public void createNewDir(ProcessedPath path, Long id) {
        String fullPath = getUserRootDir(id) + path.requestedPath();
        if (isResourceExist(path, id)) {
            throw new AlreadyExistException("Resource already exists: " + fullPath);
        }
        createDirectory(fullPath);
    }

    public Item getResource(ProcessedPath path, Long id) {
        String userRootPath = getUserRootDir(id);
        String fullPath = userRootPath + path.requestedPath();
        String parentPath = userRootPath + path.path();
        Optional<Item> item = findResource(fullPath, parentPath);
        if (item.isPresent() && fullPath.equals(item.get().objectName())) {
            return item.get();
        }
        throw new NotFoundException("Resource doesn't exists: " + path.requestedPath());
    }

    public boolean isResourceExist(ProcessedPath path, Long id) {
        String userRootPath = getUserRootDir(id);
        String fullPath = userRootPath + path.requestedPath();
        String parentPath = userRootPath + path.path();
        Optional<Item> item = findResource(fullPath, parentPath);
        return item.isPresent();
    }

    private Optional<Item> findResource(String fullPath, String parentPath) {
        String pathWithoutEndSlash = getPathWithoutEndSlash(fullPath);
        Iterable<Result<Item>> results =
                minioClient.listObjects(
                        ListObjectsArgs.builder()
                                .bucket(bucketName)
                                .prefix(parentPath)
                                .build());

        if (!results.iterator().hasNext()) {
            throw new NotFoundException("Parent path doesn't exist: " + parentPath);
        }
        return operationTemplate.execute(() -> {
                    for (Result<Item> item : results) {
                        String itemPath = item.get().objectName();
                        if (pathWithoutEndSlash.equals(itemPath) ||
                            (pathWithoutEndSlash + SLASH).equals(itemPath)) {
                            return Optional.of(item.get());
                        }
                    }
                    return Optional.empty();
                },
                "Minio error during getting of resource: " + fullPath,
                "Unexpected error during getting of resource: " + fullPath);
    }


    private void createDirectory(String path) {
        operationTemplate.execute(() ->
                        minioClient.putObject(
                                PutObjectArgs.builder()
                                        .bucket(bucketName)
                                        .object(path)
                                        .stream(new ByteArrayInputStream(new byte[]{}), 0, -1)
                                        .build()),
                "Minio error during creation of directory",
                "Unexpected error during creation of directory");
    }

    public void createFile(ProcessedPath path, Long id) {
        String fullPath = getUserRootDir(id) + path.requestedPath();
        if (isResourceExist(path, id)) {
            throw new AlreadyExistException("Resource already exists: " + fullPath);
        }

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
                "Minio error during deletion of resource: ",
                "Unexpected error during deletion of resource: ");
    }

    private String getUserRootDir(Long id) {
        return userRootTemplate.formatted(id);
    }

    private boolean isResourceExist(String path) {
        Optional<Item> item = findResource(path, path);
        return item.isPresent();
    }

    @NotNull
    private String getPathWithoutEndSlash(String path) {
        String pathWithoutSlash;
        if (path.endsWith(SLASH)) {
            pathWithoutSlash = path.substring(0, path.length() - 1);
        } else {
            pathWithoutSlash = path;
        }
        return pathWithoutSlash;
    }

}
