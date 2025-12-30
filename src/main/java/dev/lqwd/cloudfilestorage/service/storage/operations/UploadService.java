package dev.lqwd.cloudfilestorage.service.storage.operations;

import dev.lqwd.cloudfilestorage.dto.resource.ResourceResponseDto;
import dev.lqwd.cloudfilestorage.mapper.ResourceResponseMapper;
import dev.lqwd.cloudfilestorage.path_processor.PathProcessor;
import dev.lqwd.cloudfilestorage.path_processor.ProcessedPath;
import dev.lqwd.cloudfilestorage.service.storage.provider.minio.ValidationMinioService;
import dev.lqwd.cloudfilestorage.service.storage.provider.minio.UploadMinioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class UploadService {

    private final UploadMinioService uploadStorageService;
    private final ValidationMinioService validationStorageService;
    private final PathProcessor pathProcessor;
    private final ResourceResponseMapper mapper;
    private final CreationService creationService;

    public List<ResourceResponseDto> upload(String rawPath, long id, MultipartFile[] files) {
        String requestedFolderPath = pathProcessor.processDir(rawPath).requestedPath();
        validationStorageService.validateOnAbsence(requestedFolderPath, id);
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