package dev.lqwd.cloudfilestorage.service.storage;

import dev.lqwd.cloudfilestorage.dto.resource.ResourceResponseDto;
import dev.lqwd.cloudfilestorage.mapper.ResourceResponseMapper;
import dev.lqwd.cloudfilestorage.path_processor.PathProcessor;
import dev.lqwd.cloudfilestorage.path_processor.ProcessedPath;
import dev.lqwd.cloudfilestorage.service.proxy.StorageValidationProxyService;
import dev.lqwd.cloudfilestorage.service.proxy.UploadProxyService;
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

    private final UploadProxyService uploadProxyService;
    private final StorageValidationProxyService validationProxyService;
    private final PathProcessor pathProcessor;
    private final ResourceResponseMapper mapper;
    private final CreationService creationService;


    public List<ResourceResponseDto> upload(String rawPath, long id, MultipartFile[] files) {
        String requestedFolderPath = pathProcessor.processDir(rawPath).requestedPath();

        validationProxyService.validateOnAbsence(requestedFolderPath, id);
        for (MultipartFile file : files) {
            validationProxyService.validateOnExistence(getFilePath(file, requestedFolderPath), id);
        }

        return Arrays.stream(files)
                .map(file -> {
                    String filePath = getFilePath(file, requestedFolderPath);
                    ProcessedPath processed = pathProcessor.processFile(filePath);
                    creationService.createRecursiveParentFolders(processed.parentPath(), id);
                    uploadProxyService.uploadResource(requestedFolderPath, id, file);
                    return mapper.toResponseDTO(processed, file.getSize());
                })
                .toList();
    }

    private static String getFilePath(MultipartFile file, String requestedFolderPath) {
        return requestedFolderPath + file.getOriginalFilename();
    }

}