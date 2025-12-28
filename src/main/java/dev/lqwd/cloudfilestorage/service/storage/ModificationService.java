package dev.lqwd.cloudfilestorage.service.storage;

import dev.lqwd.cloudfilestorage.dto.resource.ResourceResponseDto;
import dev.lqwd.cloudfilestorage.entity.Type;
import dev.lqwd.cloudfilestorage.exception.BadRequestException;
import dev.lqwd.cloudfilestorage.mapper.ResourceResponseMapper;
import dev.lqwd.cloudfilestorage.service.proxy.FindProxyService;
import dev.lqwd.cloudfilestorage.service.proxy.ModificationsProxyService;
import dev.lqwd.cloudfilestorage.path_processor.PathProcessor;
import dev.lqwd.cloudfilestorage.path_processor.ProcessedPath;
import dev.lqwd.cloudfilestorage.service.proxy.StorageValidationProxyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@Slf4j
@RequiredArgsConstructor
public class ModificationService {

    private static final String SLASH = "/";

    private final ModificationsProxyService modificationsProxyService;
    private final StorageValidationProxyService validationProxyService;
    private final FindProxyService findProxyService;
    private final PathProcessor pathProcessor;
    private final ResourceResponseMapper mapper;


    public void removeResource(String rawPath, long id) {
        ProcessedPath path = pathProcessor.processResource(rawPath);
        String requestedPath = getRequestedPath(path);
        validationProxyService.validateOnAbsence(requestedPath, id);

        if (isDirectory(path)) {
            modificationsProxyService.removeDir(requestedPath, id);
        } else {
            modificationsProxyService.removeFile(requestedPath, id);
        }
    }

    public ResourceResponseDto moveResource(String from, String to, long id) {
        ProcessedPath pathFrom = pathProcessor.processResource(from);
        ProcessedPath pathTo = pathProcessor.processResource(to);
        if (!pathFrom.type().equals(pathTo.type())) {
            throw new BadRequestException("The resource types must match");
        }
        String requestedPathFrom = getRequestedPath(pathFrom);
        String requestedPathTo = getRequestedPath(pathTo);

        if (requestedPathFrom.equals(SLASH) || requestedPathFrom.isBlank()) {
            throw new BadRequestException("You can't move the root directory");
        }

        String parentPathTo = pathTo.parentPath();
        validationProxyService.validateParentPath(id, parentPathTo);

        validationProxyService.validateOnAbsence(requestedPathFrom, id);
        validationProxyService.validateOnExistence(requestedPathTo, id);

        if (isDirectory(pathFrom)) {
            modificationsProxyService.moveDir(requestedPathFrom, requestedPathTo, id);
            return mapper.toDirResponseDTO(pathTo);
        }
        modificationsProxyService.moveFile(requestedPathFrom, requestedPathTo, id);
        return mapper.toFileResponseDTO(pathTo, findProxyService.findResource(requestedPathTo, id).size());
    }

    private static String getRequestedPath(ProcessedPath path) {
        return path.requestedPath();
    }

    private static boolean isDirectory(ProcessedPath path) {
        return path.type().equals(Type.DIRECTORY);
    }

}