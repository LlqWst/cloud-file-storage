package dev.lqwd.cloudfilestorage.service.storage.operations;

import dev.lqwd.cloudfilestorage.dto.resource.DirectoryResponseDto;
import dev.lqwd.cloudfilestorage.exception.BadRequestException;
import dev.lqwd.cloudfilestorage.infrastructure.mapper.ResourceResponseMapper;
import dev.lqwd.cloudfilestorage.service.storage.provider.minio.CreationMinioService;
import dev.lqwd.cloudfilestorage.infrastructure.path_processor.PathProcessor;
import dev.lqwd.cloudfilestorage.infrastructure.path_processor.ProcessedPath;
import dev.lqwd.cloudfilestorage.service.storage.provider.minio.ValidationMinioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/*
TODO: возможен race condition, т.к. сначала идет проверка.
 Возможно, следует блокировать папку вовремя чека.
 MINIO не выбрасывает exception, если существует файл с именем, создаваемой папки
 */

@Service
@RequiredArgsConstructor
public class CreationService {

    private static final String EMPTY = "";
    private static final String SLASH = "/";

    private final CreationMinioService creationStorageService;
    private final ValidationMinioService validationStorageService;
    private final PathProcessor pathProcessor;
    private final ResourceResponseMapper mapper;

    public void createUserRootDir(long id) {
        if (!validationStorageService.isExist(EMPTY, id)) {
            creationStorageService.createDirectory(EMPTY, id);
        }
    }

    public void createRecursiveParentFolders(String path, long id) {
        if (!validationStorageService.isExist(path, id)) {
            creationStorageService.createDirectory(path, id);
            String parentPath = pathProcessor.processDir(path).parentPath();
            createRecursiveParentFolders(parentPath, id);
        }
    }

    public DirectoryResponseDto createDir(String rawPath, long id) {
        ProcessedPath path = pathProcessor.processDir(rawPath);
        validateOnRootPath(path.resourceName());
        String requestedPath = path.requestedPath();
        validationStorageService.validateParentPath(id, path.parentPath());
        validationStorageService.validateOnExistence(requestedPath, id);

        creationStorageService.createDirectory(requestedPath, id);
        return mapper.toDirResponseDTO(path);
    }

    private static void validateOnRootPath(String path) {
        if (path.isBlank() || SLASH.equals(path)) {
            throw new BadRequestException("Resource name is empty or equals '/'");
        }
    }

}
