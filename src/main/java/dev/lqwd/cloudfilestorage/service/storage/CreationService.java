package dev.lqwd.cloudfilestorage.service.storage;

import dev.lqwd.cloudfilestorage.dto.resource.DirectoryResponseDto;
import dev.lqwd.cloudfilestorage.mapper.ResourceResponseMapper;
import dev.lqwd.cloudfilestorage.service.proxy.CreationProxyService;
import dev.lqwd.cloudfilestorage.path_processor.PathProcessor;
import dev.lqwd.cloudfilestorage.path_processor.ProcessedPath;
import dev.lqwd.cloudfilestorage.service.proxy.StorageValidationProxyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class CreationService {

    private static final String EMPTY = "";

    private final CreationProxyService creationProxyService;
    private final StorageValidationProxyService validationProxyService;
    private final PathProcessor pathProcessor;
    private final ResourceResponseMapper mapper;


    public void createUserRootDir(long id) {
        if (!validationProxyService.isExist(EMPTY, id)) {
            creationProxyService.createDirectory(EMPTY, id);
        }
    }

    public void createRecursiveParentFolders(String path, long id) {
        if (!validationProxyService.isExist(path, id)) {
            creationProxyService.createDirectory(path, id);
            String parentPath = pathProcessor.processDir(path).parentPath();
            createRecursiveParentFolders(parentPath, id);
        }
    }

    public DirectoryResponseDto createDir(String rawPath, long id) {
        ProcessedPath path = pathProcessor.processDir(rawPath);
        String parentPath = path.parentPath();
        String requestedPath = getRequestedPath(path);
        validationProxyService.validateParentPath(id, parentPath);
        validationProxyService.validateOnExistence(requestedPath, id);
        creationProxyService.createDirectory(requestedPath, id);
        return mapper.toDirResponseDTO(path);
    }

    private static String getRequestedPath(ProcessedPath path) {
        return path.requestedPath();
    }

}
