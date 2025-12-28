package dev.lqwd.cloudfilestorage.repository.minio;


import dev.lqwd.cloudfilestorage.repository.FindResourceStorageDao;
import io.minio.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class MinioFindImpl implements FindResourceStorageDao<StatObjectResponse> {

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

}