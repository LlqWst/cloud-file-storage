package dev.lqwd.cloudfilestorage.repository.minio;

import dev.lqwd.cloudfilestorage.exception.InternalErrorException;
import io.minio.*;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;


@Component
@Slf4j
@RequiredArgsConstructor
public class MinioDao {

    @Value("${app.minio.bucket.name}")
    private String bucketName;

    private final MinioClient minioClient;
    private final MinioOperationTemplate operationTemplate;


    public Optional<StatObjectResponse> findResource(String pathWithUserDir) {
        return operationTemplate.findResource(() ->
                        minioClient.statObject(
                                StatObjectArgs.builder()
                                        .bucket(bucketName)
                                        .object(pathWithUserDir)
                                        .build()),
                "Error during getting of resource: " + pathWithUserDir);
    }

    public void createDirectory(String pathWithUserDir) {
        operationTemplate.execute(() ->
                        minioClient.putObject(
                                PutObjectArgs.builder()
                                        .bucket(bucketName)
                                        .object(pathWithUserDir)
                                        .stream(new ByteArrayInputStream(new byte[]{}), 0, -1)
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
                                        .build()),
                "Error during moving resource from: %s - to: %s".formatted(source, target));
    }

    public List<Item> findResources(String pathWithUserDir, boolean isRecursive) {
        return operationTemplate.execute(() ->
                        StreamSupport.stream(
                                        getResultItems(pathWithUserDir, isRecursive)
                                                .spliterator(), false)
                                .map(this::safeGetItem)
                                .toList(),
                "Error during getting of directory's resources. path: " + pathWithUserDir);
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

    public GetObjectResponse downloadByPath(String pathWithUserDir) {
        return operationTemplate.execute(() ->
                        minioClient.getObject(
                                GetObjectArgs.builder()
                                        .bucket(bucketName)
                                        .object(pathWithUserDir)
                                        .build()),
                "Error during download file: " + pathWithUserDir);
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