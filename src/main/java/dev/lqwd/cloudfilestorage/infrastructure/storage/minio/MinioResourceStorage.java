package dev.lqwd.cloudfilestorage.infrastructure.storage.minio;

import dev.lqwd.cloudfilestorage.dto.property.StorageProperties;
import dev.lqwd.cloudfilestorage.infrastructure.storage.ResourceStorage;
import io.minio.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;


@Component
@Slf4j
@RequiredArgsConstructor
public class MinioResourceStorage implements ResourceStorage {

    private static final String EXCEPTION_IF_EXISTS = "If-None-Match";

    private final StorageProperties storageProperties;
    private final MinioClient minioClient;
    private final MinioOperationTemplate operationTemplate;

    @Override
    public void createDirectory(String pathWithUserDir) {
        operationTemplate.execute(() ->
                        minioClient.putObject(
                                PutObjectArgs.builder()
                                        .bucket(storageProperties.bucketName())
                                        .object(pathWithUserDir)
                                        .stream(new ByteArrayInputStream(new byte[]{}), 0, -1)
                                        .headers(Map.of(EXCEPTION_IF_EXISTS, "*"))
                                        .build()),
                "Error during creation of directory: " + pathWithUserDir);
    }

    @Override
    public void uploadResource(String pathWithUserDir, MultipartFile file) {
        operationTemplate.execute(() ->
                        minioClient.putObject(
                                PutObjectArgs.builder()
                                        .bucket(storageProperties.bucketName())
                                        .object(pathWithUserDir + file.getOriginalFilename())
                                        .stream(file.getInputStream(), file.getSize(), -1)
                                        .headers(Map.of(EXCEPTION_IF_EXISTS, "*"))
                                        .build()),
                "Error during uploading resource to path: " + pathWithUserDir);
    }

    @Override
    public void copyResource(String source, String target) {
        operationTemplate.execute(() ->
                        minioClient.copyObject(
                                CopyObjectArgs.builder()
                                        .bucket(storageProperties.bucketName())
                                        .object(target)
                                        .source(CopySource.builder()
                                                .bucket(storageProperties.bucketName())
                                                .object(source)
                                                .build())
                                        .build()),
                "Error during moving resource from: %s - to: %s".formatted(source, target));
    }

    @Override
    public void removeResource(String pathWithUserDir) {
        operationTemplate.execute(() ->
                        minioClient.removeObject(
                                RemoveObjectArgs.builder()
                                        .bucket(storageProperties.bucketName())
                                        .object(pathWithUserDir)
                                        .build()),
                "Error during deletion of resource: " + pathWithUserDir);
    }

    @Override
    public InputStream downloadByPath(String pathWithUserDir) {
        return operationTemplate.execute(() ->
                        minioClient.getObject(
                                GetObjectArgs.builder()
                                        .bucket(storageProperties.bucketName())
                                        .object(pathWithUserDir)
                                        .build()),
                "Error during download file: " + pathWithUserDir);
    }

}