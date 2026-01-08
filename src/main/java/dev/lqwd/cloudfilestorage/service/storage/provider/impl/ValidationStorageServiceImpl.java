package dev.lqwd.cloudfilestorage.service.storage.provider.impl;

import dev.lqwd.cloudfilestorage.exception.AlreadyExistException;
import dev.lqwd.cloudfilestorage.exception.NotFoundException;
import dev.lqwd.cloudfilestorage.infrastructure.storage.FindStorage;
import dev.lqwd.cloudfilestorage.infrastructure.path.PathNormalizer;
import dev.lqwd.cloudfilestorage.infrastructure.UserDirectoryProvider;
import io.minio.StatObjectResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static dev.lqwd.cloudfilestorage.util.PathConstant.SLASH;
import static dev.lqwd.cloudfilestorage.util.RepeatableErrorMessage.*;


@Service
@Slf4j
@RequiredArgsConstructor
public class ValidationStorageServiceImpl implements dev.lqwd.cloudfilestorage.service.storage.provider.ValidationStorageService {

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
            throw new AlreadyExistException(RESOURCE_ALREADY_EXISTS_ERROR_MESSAGE + path);
        }
    }

    @Override
    public void validateOnAbsence(String path, long id) {
        if (!isExist(path, id)) {
            throw new NotFoundException(RESOURCE_NOT_EXISTS_ERROR_MESSAGE + path);
        }
    }

    @Override
    public void validateParentPath(long id, String parentPath) {
        if (!isExist(parentPath, id)) {
            throw new NotFoundException(PARENT_PATH_NOT_EXISTS_ERROR_MESSAGE + parentPath);
        }
    }

}