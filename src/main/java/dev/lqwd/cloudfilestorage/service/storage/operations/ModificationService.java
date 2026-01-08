package dev.lqwd.cloudfilestorage.service.storage.operations;

import dev.lqwd.cloudfilestorage.dto.resource.ResourceResponseDto;
import dev.lqwd.cloudfilestorage.infrastructure.MoveResourceValidator;
import dev.lqwd.cloudfilestorage.infrastructure.mapper.ResourceResponseMapper;
import dev.lqwd.cloudfilestorage.service.storage.provider.FindStorageService;
import dev.lqwd.cloudfilestorage.service.storage.provider.ModificationStorageService;
import dev.lqwd.cloudfilestorage.service.storage.provider.ValidationStorageService;
import dev.lqwd.cloudfilestorage.infrastructure.path.processor.PathProcessor;
import dev.lqwd.cloudfilestorage.infrastructure.path.processor.ProcessedPath;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static dev.lqwd.cloudfilestorage.util.PathTypeUtils.isDirectory;

/*
TODO: возможен race condition, т.к. сначала идет проверка.
      Возможно, следует блокировать папку во время чека.
      MINIO перезаписывает папку, если существует файл с именем папки
 */

@Service
@Slf4j
@RequiredArgsConstructor
public class ModificationService {

    private final ModificationStorageService modificationsStorageService;
    private final FindStorageService findStorageService;
    private final ValidationStorageService validationStorageService;
    private final MoveResourceValidator moveValidator;
    private final PathProcessor pathProcessor;
    private final ResourceResponseMapper mapper;

    public void removeResource(String rawPath, long id) {
        ProcessedPath path = pathProcessor.processResource(rawPath);
        String requestedPath = path.requestedPath();
        validationStorageService.validateOnAbsence(requestedPath, id);

        log.debug("start deleting resource, {}", path.requestedPath());
        if (isDirectory(path)) {
            modificationsStorageService.removeDir(requestedPath, id);
        } else {
            modificationsStorageService.removeFile(requestedPath, id);
        }
    }

    public ResourceResponseDto moveResource(String from, String to, long id) {
        log.info("Received a request to move from {} to {}", from, to);

        ProcessedPath pathFrom = pathProcessor.processResource(from);
        ProcessedPath pathTo = pathProcessor.processResource(to);

        String requestedPathFrom = pathFrom.requestedPath();
        String requestedPathTo = pathTo.requestedPath();

        moveValidator.validate(pathFrom, pathTo, id);

        if (isDirectory(pathFrom)) {
            modificationsStorageService.moveDir(requestedPathFrom, requestedPathTo, id);
            return mapper.toDirResponseDTO(pathTo);
        }
        modificationsStorageService.moveFile(requestedPathFrom, requestedPathTo, id);
        return findStorageService.findResource(requestedPathTo, id);
    }

}
