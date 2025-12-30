package dev.lqwd.cloudfilestorage.service.storage.operations;

import dev.lqwd.cloudfilestorage.dto.resource.ResourceResponseDto;
import dev.lqwd.cloudfilestorage.service.storage.provider.minio.FindMinioService;
import dev.lqwd.cloudfilestorage.infrastructure.PathValidator;
import dev.lqwd.cloudfilestorage.path_processor.PathProcessor;
import dev.lqwd.cloudfilestorage.path_processor.ProcessedPath;
import dev.lqwd.cloudfilestorage.service.storage.provider.minio.ValidationMinioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@RequiredArgsConstructor
public class FindService {

    private final FindMinioService findStorageService;
    private final ValidationMinioService validationStorageService;
    private final PathProcessor pathProcessor;
    private final PathValidator validator;


    public ResourceResponseDto getResource(String rawPath, long id) {
        ProcessedPath path = pathProcessor.processResource(rawPath);
        return findStorageService.findResource(getRequestedPath(path), id);
    }

    public List<ResourceResponseDto> getResources(String rawPath, long id) {
        ProcessedPath path = pathProcessor.processDir(rawPath);
        String requestedPath = getRequestedPath(path);
        validationStorageService.validateOnAbsence(requestedPath, id);

        return findStorageService.findDirResourcesWithoutDir(requestedPath, id);
    }

    public List<ResourceResponseDto> searchResource(String query, long id) {
        validator.validatePath(query);

        return findStorageService.findAllResources(id)
                .filter(resource -> resource
                        .name()
                        .toLowerCase()
                        .contains(query.toLowerCase()))
                .toList();
    }

    private static String getRequestedPath(ProcessedPath path) {
        return path.requestedPath();
    }

}
