package dev.lqwd.cloudfilestorage.service.storage.operations;

import dev.lqwd.cloudfilestorage.dto.resource.DownloadedResponseDto;
import dev.lqwd.cloudfilestorage.infrastructure.path.processor.PathProcessor;
import dev.lqwd.cloudfilestorage.infrastructure.path.processor.ProcessedPath;
import dev.lqwd.cloudfilestorage.service.storage.provider.DownloadStorageService;
import dev.lqwd.cloudfilestorage.service.storage.provider.ValidationStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static dev.lqwd.cloudfilestorage.util.PathTypeUtils.isDirectory;


@Service
@RequiredArgsConstructor
public class DownloadService {

    private static final String ZIP_EXTENSION = ".zip";

    private final PathProcessor pathProcessor;
    private final DownloadStorageService downloadStorageService;
    private final ValidationStorageService validationStorageService;

    public DownloadedResponseDto download(String rawPath, long id) {
        ProcessedPath path = pathProcessor.processResource(rawPath);
        String requestedPath = path.requestedPath();
        validationStorageService.validateOnAbsence(requestedPath, id);

        if (isDirectory(path)) {
            return DownloadedResponseDto.builder()
                    .content(downloadStorageService.getZipBytes(id, requestedPath, path))
                    .name(path.resourceName() + ZIP_EXTENSION)
                    .build();
        }
        return DownloadedResponseDto.builder()
                .content(downloadStorageService.getFileBytes(id, requestedPath))
                .name(path.resourceName())
                .build();
    }

}
