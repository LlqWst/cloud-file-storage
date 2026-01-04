package dev.lqwd.cloudfilestorage.service.storage.operations;

import dev.lqwd.cloudfilestorage.dto.resource.ResourceResponseDto;
import dev.lqwd.cloudfilestorage.entity.Type;
import dev.lqwd.cloudfilestorage.exception.BadRequestException;
import dev.lqwd.cloudfilestorage.infrastructure.mapper.ResourceResponseMapper;
import dev.lqwd.cloudfilestorage.service.storage.provider.minio.FindMinioService;
import dev.lqwd.cloudfilestorage.service.storage.provider.minio.ModificationsMinioService;
import dev.lqwd.cloudfilestorage.infrastructure.path_processor.PathProcessor;
import dev.lqwd.cloudfilestorage.infrastructure.path_processor.ProcessedPath;
import dev.lqwd.cloudfilestorage.service.storage.provider.minio.ValidationMinioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/*
TODO: возможен race condition, т.к. сначала идет проверка.
 Возможно, следует блокировать папку во время чека.
 MINIO перезаписывает папку, если существует файл с именем папки
 */

@Service
@Slf4j
@RequiredArgsConstructor
public class ModificationService {

    private static final String SLASH = "/";

    private final ModificationsMinioService modificationsStorageService;
    private final ValidationMinioService validationStorageService;
    private final FindMinioService findStorageService;
    private final PathProcessor pathProcessor;
    private final ResourceResponseMapper mapper;

    public void removeResource(String rawPath, long id) {
        ProcessedPath path = pathProcessor.processResource(rawPath);
        String requestedPath = getRequestedPath(path);
        validationStorageService.validateOnAbsence(requestedPath, id);

        log.debug("start deleting resource, {}", path.requestedPath());
        if (isDirectory(path)) {
            modificationsStorageService.removeDir(requestedPath, id);
        } else {
            modificationsStorageService.removeFile(requestedPath, id);
        }
    }

    public ResourceResponseDto moveResource(String from, String to, long id) {
        log.info("Пришел запрос на move из {} в {}", from, to);

        ProcessedPath pathFrom = pathProcessor.processResource(from);
        ProcessedPath pathTo = pathProcessor.processResource(to);

        validateOnEqualsType(pathFrom.type(), pathTo.type());

        String requestedPathFrom = getRequestedPath(pathFrom);
        String requestedPathTo = getRequestedPath(pathTo);

        log.info("После Processed requested path из {} в {}", requestedPathFrom, requestedPathTo);

        if(requestedPathFrom.equals(pathTo.parentPath())){
            throw new BadRequestException("You cannot copy resource to itself");
        }

        String toParentPath = pathTo.parentPath();
        validateOnMoveToItself(requestedPathFrom, toParentPath);
        validateOnRootPath(requestedPathFrom);

        validationStorageService.validateParentPath(id, toParentPath);
        validationStorageService.validateOnAbsence(requestedPathFrom, id);
        validationStorageService.validateOnExistence(requestedPathTo, id);

        if (isDirectory(pathFrom)) {
            modificationsStorageService.moveDir(requestedPathFrom, requestedPathTo, id);
            return mapper.toDirResponseDTO(pathTo);
        }
        modificationsStorageService.moveFile(requestedPathFrom, requestedPathTo, id);
        return findStorageService.findResource(requestedPathTo, id);
    }

    private static void validateOnMoveToItself(String requestedPathFrom, String toParentPath) {
        if(requestedPathFrom.equals(toParentPath)){
            throw new BadRequestException("You cannot copy resource to itself");
        }
    }

    private static void validateOnRootPath(String requestedPathFrom) {
        if (requestedPathFrom.equals(SLASH) || requestedPathFrom.isBlank()) {
            throw new BadRequestException("You can't move the root directory");
        }
    }

    private static void validateOnEqualsType(Type typeFrom, Type typeTo) {
        if (!typeFrom.equals(typeTo)) {
            throw new BadRequestException("The resource types must match");
        }
    }

    private static String getRequestedPath(ProcessedPath path) {
        return path.requestedPath();
    }

    private static boolean isDirectory(ProcessedPath path) {
        return path.type().equals(Type.DIRECTORY);
    }

}