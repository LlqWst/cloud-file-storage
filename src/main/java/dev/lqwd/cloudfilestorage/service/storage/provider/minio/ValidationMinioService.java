package dev.lqwd.cloudfilestorage.service.storage.provider.minio;

import dev.lqwd.cloudfilestorage.exception.AlreadyExistException;
import dev.lqwd.cloudfilestorage.exception.NotFoundException;
import dev.lqwd.cloudfilestorage.repository.storage.minio.MinioFindStorage;
import dev.lqwd.cloudfilestorage.infrastructure.PathNormalizer;
import dev.lqwd.cloudfilestorage.infrastructure.UserDirectoryProvider;
import dev.lqwd.cloudfilestorage.service.storage.provider.ValidationStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@Slf4j
@RequiredArgsConstructor
public class ValidationMinioService implements ValidationStorageService {

    private static final String SLASH = "/";

    private final PathNormalizer pathNormalizer;
    private final UserDirectoryProvider userDirectoryProvider;
    private final MinioFindStorage minioFindStorage;

    public boolean isExistIgnoreEndSlash(String path, long id) {
        String pathWithoutEndSlash = pathNormalizer.getPathWithoutEndSlash(path);
        String pathWithEndSlash = pathWithoutEndSlash + SLASH;
        return isExist(pathWithoutEndSlash, id) || isExist(pathWithEndSlash, id);
    }

    public boolean isExist(String path, long id) {
        log.debug("checking for the existence of a resource");
        return minioFindStorage.findResource(userDirectoryProvider.provide(path, id))
                .isPresent();
    }

    public void validateOnExistence(String path, long id) {
        if (isExistIgnoreEndSlash(path, id)) {
            throw new AlreadyExistException("Resource already exists: " + path);
        }
    }

    public void validateOnAbsence(String path, long id) {
        if (!isExist(path, id)) {
            throw new NotFoundException("Resource doesn't exists: " + path);
        }
    }

    public void validateParentPath(long id, String parentPath) {
        if (!isExist(parentPath, id)) {
            throw new NotFoundException("Parent path doesn't exist: " + parentPath);
        }
    }

}