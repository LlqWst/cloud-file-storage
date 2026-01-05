package dev.lqwd.cloudfilestorage.service.storage.provider.impl;

import dev.lqwd.cloudfilestorage.exception.AlreadyExistException;
import dev.lqwd.cloudfilestorage.exception.NotFoundException;
import dev.lqwd.cloudfilestorage.repository.storage.FindStorage;
import dev.lqwd.cloudfilestorage.infrastructure.PathNormalizer;
import dev.lqwd.cloudfilestorage.infrastructure.UserDirectoryProvider;
import io.minio.StatObjectResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@Slf4j
@RequiredArgsConstructor
public class ValidationStorageServiceImpl implements dev.lqwd.cloudfilestorage.service.storage.provider.ValidationStorageService {

    private static final String SLASH = "/";

    private final PathNormalizer pathNormalizer;
    private final UserDirectoryProvider userDirectoryProvider;
    private final FindStorage<StatObjectResponse> findStorage;

    @Override
    public boolean isExistIgnoreEndSlash(String path, long id) {
        String pathWithoutEndSlash = pathNormalizer.getPathWithoutEndSlash(path);
        String pathWithEndSlash = pathWithoutEndSlash + SLASH;
        return isExist(pathWithoutEndSlash, id) || isExist(pathWithEndSlash, id);
    }

    @Override
    public boolean isExist(String path, long id) {
        log.debug("checking for the existence of a resource");
        return findStorage.findResource(userDirectoryProvider.provide(path, id))
                .isPresent();
    }

    @Override
    public void validateOnExistence(String path, long id) {
        if (isExistIgnoreEndSlash(path, id)) {
            throw new AlreadyExistException("Resource already exists: " + path);
        }
    }

    @Override
    public void validateOnAbsence(String path, long id) {
        if (!isExist(path, id)) {
            throw new NotFoundException("Resource doesn't exists: " + path);
        }
    }

    @Override
    public void validateParentPath(long id, String parentPath) {
        if (!isExist(parentPath, id)) {
            throw new NotFoundException("Parent path doesn't exist: " + parentPath);
        }
    }

}