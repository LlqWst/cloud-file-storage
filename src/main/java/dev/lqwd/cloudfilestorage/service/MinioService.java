package dev.lqwd.cloudfilestorage.service;

import dev.lqwd.cloudfilestorage.exception.InternalErrorException;
import io.minio.*;
import io.minio.errors.MinioException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;


@Service
@Slf4j
@AllArgsConstructor
public class MinioService {

    public static final String BUCKET_NAME = "user-files";
    public static final String USER_ROOT_DIRECTORY = "folder-%d-path/";
    private final MinioClient minioClient;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        createRootBucket();
    }

    private void createRootBucket() {
        try {
            boolean found =
                    minioClient.bucketExists(BucketExistsArgs.builder()
                            .bucket(BUCKET_NAME)
                            .build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder()
                        .bucket(BUCKET_NAME)
                        .build());
                log.info("Bucket '{}' created", BUCKET_NAME);
            } else {
                log.info("Bucket '{}' already exists", BUCKET_NAME);
            }
        } catch (MinioException e) {
            log.error("HTTP trace: {}", e.httpTrace());
            throw new InternalErrorException("Error during creation of Minio bucket: " + BUCKET_NAME, e);
        } catch (Exception e) {
            throw new InternalErrorException("Unexpected error during creation of Minio bucket: " + BUCKET_NAME, e);
        }
    }

    public void createDirectory(String path) {
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(BUCKET_NAME)
                            .object(path)
                            .stream(new ByteArrayInputStream(new byte[]{}), 0, -1)
                            .build());
            log.info("Directory created: {}", path);
        } catch (MinioException e) {
            log.error("HTTP trace: {}", e.httpTrace());
            throw new InternalErrorException("Minio error during creation of directory", e);
        } catch (Exception e) {
            throw new InternalErrorException("Unexpected error during creation of directory", e);
        }
    }

    public void createDirectoryForNewUser(Long id) {
        createDirectory(USER_ROOT_DIRECTORY.formatted(id));
    }

    public void createNewDirectory(String path, Long id) {
        createDirectory(USER_ROOT_DIRECTORY.formatted(id) + path);
    }

    public void removeResource(String resourceName) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(BUCKET_NAME)
                            .object(resourceName)
                            .build());
        } catch (MinioException e) {
            log.error("HTTP trace: {}", e.httpTrace());
            throw new InternalErrorException("Minio error during deletion of resource: " + resourceName, e);
        } catch (Exception e) {
            throw new InternalErrorException("Unexpected error during deletion of resource: " + resourceName, e);
        }
    }

}
