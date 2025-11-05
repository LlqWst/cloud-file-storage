package dev.lqwd.cloudfilestorage.repository.minio;

import dev.lqwd.cloudfilestorage.exception.AlreadyExistException;
import dev.lqwd.cloudfilestorage.exception.NotFoundException;
import dev.lqwd.cloudfilestorage.utils.PathNormalizer;
import dev.lqwd.cloudfilestorage.utils.UserDirectoryProvider;
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
    private final UserDirectoryProvider userDirectoryProvider;


    private String getPathWithUserDir(String path, long id) {
        return userDirectoryProvider.provide(path, id);
    }

    public boolean isExistIgnoreEndSlash(String path, long id) {
        String pathWithoutEndSlash = pathNormalizer.getPathWithoutEndSlash(path);
        String pathWithEndSlash = pathWithoutEndSlash + SLASH;
        return isExist(pathWithoutEndSlash, id) || isExist(pathWithEndSlash, id);
    }

    public boolean isExist(String path, long id) {
        try {
            findResource(path, id);
            return true;
        } catch (NotFoundException _) {
            return false;
        }
    }

    public StatObjectResponse findResource(String path, long id) {
        String pathWithUserDir = getPathWithUserDir(path, id);
        return operationTemplate.execute(() ->
                        minioClient.statObject(StatObjectArgs.builder()
                                .bucket(bucketName)
                                .object(pathWithUserDir)
                                .build()),
                "Error during getting of resource: " + path);
    }

    public List<Item> findDirectoryResourcesWithoutDir(String path, long id) {
        String pathWithUserDir = getPathWithUserDir(path, id);
        return findResources(path, pathWithUserDir, pathWithUserDir, false);
    }

    public void createDirectory(String path, long id) {
        String pathWithUserDir = getPathWithUserDir(path, id);
        operationTemplate.execute(() ->
                        minioClient.putObject(
                                PutObjectArgs.builder()
                                        .bucket(bucketName)
                                        .object(pathWithUserDir)
                                        .stream(new ByteArrayInputStream(new byte[]{}), 0, -1)
                                        .build()),
                "Error during creation of directory: " + path);
    }

    public void buildFile(String path, long id) {
        String pathWithUserDir = getPathWithUserDir(path, id);
        StringBuilder builder = new StringBuilder();
        builder.append("some text for test");
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(
                    builder.toString().getBytes(StandardCharsets.UTF_8));

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(pathWithUserDir)
                            .stream(bais, bais.available(), -1)
                            .build());
            bais.close();
            log.info("file create for: {}", path);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void removeDir(String path, long id) {
        List<Item> items = findDirectoryResourcesWithDir(path, id);
        for (Item item : items) {
            removeResource(path, item.objectName());
        }
    }

    public void removeFile(String path, long id) {
        String pathWithUserDir = getPathWithUserDir(path, id);
        removeResource(path, pathWithUserDir);
    }

    public void moveDir(String from, String to, long id) {
        List<Item> items = findDirectoryResourcesWithDir(from, id);
        for (Item item : items) {
            String source = item.objectName();
            String target = item.objectName().replaceFirst(from, to);

            copyResource(from, to, source, target);
            removeResource(from, source);
        }
    }

    public void moveFile(String from, String to, long id) {
        String fromWithUserDir = getPathWithUserDir(from, id);
        String toWithUserDir = getPathWithUserDir(to, id);
        copyResource(from, to, fromWithUserDir, toWithUserDir);
        removeResource(from, fromWithUserDir);
    }

    private void copyResource(String from, String to,
                              String source, String target) {

        operationTemplate.execute(() -> {
                    minioClient.copyObject(
                            CopyObjectArgs.builder()
                                    .bucket(bucketName)
                                    .object(target)
                                    .source(
                                            CopySource.builder()
                                                    .bucket(bucketName)
                                                    .object(source)
                                                    .build())
                                    .build());
                },
                "Error during moving resource from: %s - to: %s".formatted(from, to));
    }

    private List<Item> findResources(String path, String pathWithUserDir,
                                     String exceptionDir, boolean isRecursive) {

        Iterable<Result<Item>> results = getResultItems(pathWithUserDir, isRecursive);
        return operationTemplate.execute(() -> {
                    List<Item> items = new ArrayList<>();
                    for (Result<Item> item : results) {
                        if (!item.get().objectName().equals(exceptionDir)) {
                            items.add(item.get());
                        }
                    }
                    return items;
                },
                "Error during getting of directory's resources. path: " + path);
    }

    private List<Item> findDirectoryResourcesWithDir(String path, long id) {
        String userDir = userDirectoryProvider.provide(id);
        String pathWithUserDir = getPathWithUserDir(path, id);
        return findResources(path, pathWithUserDir, userDir, true);
    }

    public void validateOnExistence(String path, long id) {
        if (isExistIgnoreEndSlash(path, id)) {
            throw new AlreadyExistException("Resource already exist: " + path);
        }
    }

    public void validateOnAbsence(String path, long id) {
        if (!isExistIgnoreEndSlash(path, id)) {
            throw new NotFoundException("Resource doesn't exist: " + path);
        }
    }

    @NotNull
    private Iterable<Result<Item>> getResultItems(String pathWithUserDir, boolean isRecursive) {
        return minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(bucketName)
                        .prefix(pathWithUserDir)
                        .recursive(isRecursive)
                        .build());
    }

    private void removeResource(String path, String pathWithUserDir) {
        operationTemplate.execute(() ->
                        minioClient.removeObject(
                                RemoveObjectArgs.builder()
                                        .bucket(bucketName)
                                        .object(pathWithUserDir)
                                        .build()),
                "Error during deletion of resource: " + path);
    }

}