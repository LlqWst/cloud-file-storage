package dev.lqwd.cloudfilestorage.repository.minio;

import dev.lqwd.cloudfilestorage.repository.BaseFileStorageDao;
import io.minio.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;


@Component
@Slf4j
@RequiredArgsConstructor
public class MinioBaseDaoImpl implements BaseFileStorageDao {

    @Value("${app.minio.bucket.name}")
    private String bucketName;

    private final MinioClient minioClient;
    private final MinioOperationTemplate operationTemplate;

    public void createDirectory(String pathWithUserDir) {
        operationTemplate.execute(() ->
                        minioClient.putObject(
                                PutObjectArgs.builder()
                                        .bucket(bucketName)
                                        .object(pathWithUserDir)
                                        .stream(new ByteArrayInputStream(new byte[]{}), 0, -1)
                                        .headers(Map.of("x-amz-if-none-match", "*"))
                                        .build()),
                "Error during creation of directory: " + pathWithUserDir);
    }

    public void uploadResource(String pathWithUserDir, MultipartFile file) {
        operationTemplate.execute(() ->
                        minioClient.putObject(
                                PutObjectArgs.builder()
                                        .bucket(bucketName)
                                        .object(pathWithUserDir + file.getOriginalFilename())
                                        .stream(file.getInputStream(), file.getSize(), -1)
                                        .headers(Map.of("x-amz-if-none-match", "*"))
                                        .build()),
                "Error during uploading resource to path: " + pathWithUserDir);
    }

    public void copyResource(String source, String target) {
        operationTemplate.execute(() ->
                        minioClient.copyObject(
                                CopyObjectArgs.builder()
                                        .bucket(bucketName)
                                        .object(target)
                                        .source(CopySource.builder()
                                                .bucket(bucketName)
                                                .object(source)
                                                .build())
                                        .headers(Map.of("x-amz-if-none-match", "*"))
                                        .build()),
                "Error during moving resource from: %s - to: %s".formatted(source, target));
    }

    public void removeResource(String pathWithUserDir) {
        operationTemplate.execute(() ->
                        minioClient.removeObject(
                                RemoveObjectArgs.builder()
                                        .bucket(bucketName)
                                        .object(pathWithUserDir)
                                        .build()),
                "Error during deletion of resource: " + pathWithUserDir);
    }

    public InputStream downloadByPath(String pathWithUserDir) {
        return operationTemplate.execute(() ->
                        minioClient.getObject(
                                GetObjectArgs.builder()
                                        .bucket(bucketName)
                                        .object(pathWithUserDir)
                                        .build()),
                "Error during download file: " + pathWithUserDir);
    }

}