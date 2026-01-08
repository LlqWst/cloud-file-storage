package dev.lqwd.cloudfilestorage.service.storage.operations;

import dev.lqwd.cloudfilestorage.dto.resource.ResourceResponseDto;
import dev.lqwd.cloudfilestorage.infrastructure.MultipartFileValidator;
import dev.lqwd.cloudfilestorage.infrastructure.mapper.ResourceResponseMapper;
import dev.lqwd.cloudfilestorage.infrastructure.path.processor.PathProcessor;
import dev.lqwd.cloudfilestorage.infrastructure.path.processor.ProcessedPath;
import dev.lqwd.cloudfilestorage.service.storage.provider.UploadStorageService;
import dev.lqwd.cloudfilestorage.service.storage.provider.ValidationStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;


@Service
@RequiredArgsConstructor
public class UploadService {

    private final UploadStorageService uploadStorageService;
    private final ValidationStorageService validationStorageService;
    private final PathProcessor pathProcessor;
    private final ResourceResponseMapper mapper;
    private final CreationService creationService;
    private final MultipartFileValidator fileValidator;

    public List<ResourceResponseDto> upload(String rawPath, long id, MultipartFile[] files) {
        String requestedFolderPath = pathProcessor.processDir(rawPath).requestedPath();

        validationStorageService.validateOnAbsence(requestedFolderPath, id);
        fileValidator.validate(files);
        validateOnExistence(id, files, requestedFolderPath);

        return Arrays.stream(files)
                .map(file -> {
                    String filePath = getFilePath(file, requestedFolderPath);
                    ProcessedPath processed = pathProcessor.processFile(filePath);
                    creationService.createRecursiveParentFolders(processed.parentPath(), id);
                    uploadStorageService.uploadResource(requestedFolderPath, id, file);
                    return mapper.toResponseDTO(processed, file.getSize());
                })
                .toList();
    }

    private void validateOnExistence(long id, MultipartFile[] files, String requestedFolderPath) {
        for (MultipartFile file : files) {
            validationStorageService.validateOnExistence(getFilePath(file, requestedFolderPath), id);
        }
    }

    private static String getFilePath(MultipartFile file, String requestedFolderPath) {
        return requestedFolderPath + file.getOriginalFilename();
    }

}