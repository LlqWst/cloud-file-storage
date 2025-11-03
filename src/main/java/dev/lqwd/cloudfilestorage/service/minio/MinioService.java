package dev.lqwd.cloudfilestorage.service.minio;

import dev.lqwd.cloudfilestorage.dto.resource.ResourceResponseDTO;
import dev.lqwd.cloudfilestorage.exception.AlreadyExistException;
import dev.lqwd.cloudfilestorage.exception.NotFoundException;
import dev.lqwd.cloudfilestorage.utils.PathNormalizer;
import dev.lqwd.cloudfilestorage.utils.path_processor.ProcessedPath;
import dev.lqwd.cloudfilestorage.utils.parser.ItemParser;
import io.minio.*;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.function.Predicate.not;


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
    private final ItemParser itemParser;
    private final PathNormalizer pathNormalizer;

    public void createUserRootDir(Long id) {
        String pathWithoutSlash = getUserRootDir(id);
        if (!isResourceExist(pathWithoutSlash)) {
            createDirectory(pathWithoutSlash);
        }
    }

    public void createNewDir(ProcessedPath path, Long id) {
        String fullPath = getPathWithUserDir(path.requestedPath(), id);
        if (isResourceExist(path, id)) {
            throw new AlreadyExistException("Resource already exists: " + fullPath);
        }
        createDirectory(fullPath);
    }

    public ResourceResponseDTO getResource(ProcessedPath path, Long id) {
        String fullPath = getPathWithUserDir(path.requestedPath(), id);
        String parentPath = getPathWithUserDir(path.parentPath(), id);
        Optional<Item> item = findResource(fullPath, parentPath);
        if (item.isPresent() && fullPath.equals(item.get().objectName())) {
            return itemParser.pars(item.get());
        }
        throw new NotFoundException("Resource doesn't exists: " + path.requestedPath());
    }

    public boolean isResourceExist(ProcessedPath path, Long id) {
        String userRootPath = getUserRootDir(id);
        String fullPath = userRootPath + path.requestedPath();
        String parentPath = userRootPath + path.parentPath();
        Optional<Item> item = findResource(fullPath, parentPath);
        return item.isPresent();
    }

    private Optional<Item> findResource(String fullPath, String parentPath) {
        String pathWithoutEndSlash = pathNormalizer.getPathWithoutEndSlash(fullPath);
        Iterable<Result<Item>> results =
                minioClient.listObjects(
                        ListObjectsArgs.builder()
                                .bucket(bucketName)
                                .prefix(parentPath)
                                .build());

        if (!results.iterator().hasNext()) {
            throw new NotFoundException("Parent parentPath doesn't exist: " + parentPath);
        }
        return operationTemplate.execute(() -> {
                    for (Result<Item> item : results) {
                        String itemPath = item.get().objectName();
                        if (isRequestedResource(pathWithoutEndSlash, itemPath)) {
                            return Optional.of(item.get());
                        }
                    }
                    return Optional.empty();
                },
                "Minio error during getting of resource: " + fullPath,
                "Unexpected error during getting of resource: " + fullPath);
    }

    public List<ResourceResponseDTO> getResources(String requestedPath, long id) {
        String fullPath = getPathWithUserDir(requestedPath, id);
        String normalizedFullPath = pathNormalizer.normalize(fullPath);
        return findDirectoryResources(normalizedFullPath).stream()
                .filter(not(item -> item.objectName().equals(normalizedFullPath)))
                .map(itemParser::pars)
                .toList();
    }

    private List<Item> findDirectoryResources(String fullPath) {
        Iterable<Result<Item>> results =
                minioClient.listObjects(
                        ListObjectsArgs.builder()
                                .bucket(bucketName)
                                .prefix(fullPath)
                                .build());

        if (!results.iterator().hasNext()) {
            throw new NotFoundException("Directory doesn't exist: " + fullPath);
        }
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
        String fullPath = getPathWithUserDir(path.requestedPath(), id);
        if (isResourceExist(path, id)) {
            throw new AlreadyExistException("Resource already exists: " + fullPath);
        }
        buildFile(fullPath);
    }

    private void buildFile(String fullPath){
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

    public String getPathWithUserDir(String path, Long id) {
        return getUserRootDir(id) + path;
    }

    private static boolean isRequestedResource(String pathWithoutEndSlash, String itemPath) {
        return pathWithoutEndSlash.equals(itemPath) ||
               (pathWithoutEndSlash + SLASH).equals(itemPath);
    }

    private boolean isResourceExist(String path) {
        try {
            Optional<Item> item = findResource(path, path);
            return item.isPresent();
        } catch (NotFoundException _) {
            return false;
        }
    }

}
