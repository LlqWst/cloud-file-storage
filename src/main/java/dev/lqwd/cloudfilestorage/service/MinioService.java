package dev.lqwd.cloudfilestorage.service;

import dev.lqwd.cloudfilestorage.dto.resource.DirectoryResourceDto;
import dev.lqwd.cloudfilestorage.dto.resource.DownloadedResponseDto;
import dev.lqwd.cloudfilestorage.dto.resource.ResourceResponseDto;
import dev.lqwd.cloudfilestorage.entity.Type;
import dev.lqwd.cloudfilestorage.exception.BadRequestException;
import dev.lqwd.cloudfilestorage.exception.InternalErrorException;
import dev.lqwd.cloudfilestorage.exception.NotFoundException;
import dev.lqwd.cloudfilestorage.mapper.ResourceResponseMapper;
import dev.lqwd.cloudfilestorage.repository.minio.MinioDAO;
import dev.lqwd.cloudfilestorage.utils.PathValidator;
import dev.lqwd.cloudfilestorage.utils.parser.minio.ItemParser;
import dev.lqwd.cloudfilestorage.utils.path_processor.PathProcessor;
import dev.lqwd.cloudfilestorage.utils.path_processor.ProcessedPath;
import dev.lqwd.cloudfilestorage.utils.parser.minio.StatObjectParser;
import io.minio.StatObjectResponse;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


@Service
@Slf4j
@RequiredArgsConstructor
public class MinioService {

    private static final String EMPTY = "";
    private static final String SLASH = "/";

    private final StatObjectParser statObjectParser;
    private final ItemParser itemParser;
    private final MinioDAO minioDAO;
    private final PathProcessor pathProcessor;
    private final ResourceResponseMapper mapper;
    private final PathValidator validator;

    public void createUserRootDir(long id) {
        if (!minioDAO.isExist(EMPTY, id)) {
            minioDAO.createDirectory(EMPTY, id);
        }
    }

    public DirectoryResourceDto createDir(String rawPath, long id) {
        ProcessedPath path = pathProcessor.processDir(rawPath);
        String parentPath = path.parentPath();
        String requestedPath = getRequestedPath(path);
        validateParentPath(id, parentPath);
        minioDAO.validateOnExistence(requestedPath, id);
        minioDAO.createDirectory(requestedPath, id);
        return mapper.toDirResponseDTO(path);
    }

    public ResourceResponseDto getResource(String rawPath, long id) {
        ProcessedPath path = pathProcessor.processResource(rawPath);
        StatObjectResponse statObject = minioDAO.findResource(getRequestedPath(path), id);
        return statObjectParser.pars(statObject);
    }

    public List<ResourceResponseDto> getResources(String rawPath, long id) {
        ProcessedPath path = pathProcessor.processDir(rawPath);
        String requestedPath = getRequestedPath(path);
        minioDAO.validateOnAbsence(requestedPath, id);
        return minioDAO.findDirectoryResourcesWithoutDir(requestedPath, id)
                .stream()
                .map(itemParser::pars)
                .toList();
    }

    public void removeResource(String rawPath, long id) {
        ProcessedPath path = pathProcessor.processResource(rawPath);
        String requestedPath = getRequestedPath(path);
        minioDAO.validateOnAbsence(requestedPath, id);
        if (isDirectory(path)) {
            minioDAO.removeDir(requestedPath, id);
        } else {
            minioDAO.removeFile(requestedPath, id);
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
            throw new BadRequestException("You can't cut the root directory");
        }

        String parentPathTo = pathTo.parentPath();
        validateParentPath(id, parentPathTo);

        minioDAO.validateOnAbsence(requestedPathFrom, id);
        minioDAO.validateOnExistence(requestedPathTo, id);

        if (isDirectory(pathFrom)) {
            minioDAO.moveDir(requestedPathFrom, requestedPathTo, id);
            return mapper.toDirResponseDTO(pathTo);
        } else {
            minioDAO.moveFile(requestedPathFrom, requestedPathTo, id);
            return mapper.toFileResponseDTO(pathTo, minioDAO.findResource(requestedPathTo, id).size());
        }
    }

    private void validateParentPath(long id, String parentPathTo) {
        if (!minioDAO.isExist(parentPathTo, id)) {
            throw new NotFoundException("Parent path doesn't exist: " + parentPathTo);
        }
    }

    public List<ResourceResponseDto> searchResource(String query, long id) {
        validator.validatePath(query);
        String lowerCaseQuery = query.toLowerCase();
        return minioDAO.findAllResources(id)
                .stream()
                .map(itemParser::pars)
                .filter(resource -> resource
                        .name()
                        .toLowerCase()
                        .contains(lowerCaseQuery))
                .toList();
    }

    private static String getRequestedPath(ProcessedPath path) {
        return path.requestedPath();
    }

    public DownloadedResponseDto download(String rawPath, long id) {
        ProcessedPath path = pathProcessor.processResource(rawPath);
        String requestedPath = path.requestedPath();
        minioDAO.validateOnAbsence(requestedPath, id);

        if (isDirectory(path)) {
            return DownloadedResponseDto.builder()
                    .content(outputStream -> {
                                try (ZipOutputStream zipOut = new ZipOutputStream(outputStream)) {
                                    List<Item> resources = minioDAO.findDirectoryResourcesWithoutDirRecursive(requestedPath, id);
                                    for (Item resource : resources) {
                                        String resourcePath = resource.objectName();
                                        String folderStartPath = path.resourceName() + "/";
                                        int indexOfFolder = resourcePath.indexOf(folderStartPath);
                                        String entryName = resourcePath
                                                .substring(indexOfFolder)
                                                .replaceFirst(folderStartPath, EMPTY);
                                        ZipEntry zipEntry = new ZipEntry(entryName);
                                        zipOut.putNextEntry(zipEntry);
                                        try (InputStream fileStream = minioDAO.downloadFile(resourcePath)) {
                                            fileStream.transferTo(zipOut);
                                        }
                                        zipOut.closeEntry();
                                        zipOut.flush();
                                    }
                                } catch (Exception e) {
                                    throw new InternalErrorException("Error during directory streaming for path" + requestedPath, e);
                                }
                            }
                    )
                    .name(path.resourceName() + ".zip")
                    .build();
        } else {
            return DownloadedResponseDto.builder()
                    .content(getFileBytes(id, requestedPath))
                    .name(path.resourceName())
                    .build();
        }
    }

    public List<ResourceResponseDto> upload(String rawPath, long id, MultipartFile[] files) {
        String requestedFolderPath = pathProcessor.processDir(rawPath).requestedPath();
        minioDAO.validateOnAbsence(requestedFolderPath, id);
        for (MultipartFile file : files) {
            minioDAO.validateOnExistence(file.getOriginalFilename(), id);
        }
        List<ResourceResponseDto> resources = new ArrayList<>();
        for (MultipartFile file : files) {
            String filePath = file.getOriginalFilename();
            ProcessedPath processed = pathProcessor.processFile(filePath);
            createRecursiveParentFolders(processed.parentPath(), id);
            minioDAO.uploadResource(requestedFolderPath, id, file);
            ResourceResponseDto responseDTO = mapper.toResponseDTO(processed, file.getSize());
            resources.add(responseDTO);
        }
        return resources;
    }

    private void createRecursiveParentFolders(String path, long id){
        if (!minioDAO.isExist(path, id)) {
            minioDAO.createDirectory(path, id);
            String parentPath = pathProcessor.processDir(path).parentPath();
            createRecursiveParentFolders(parentPath, id);
        }
    }

    private static boolean isDirectory(ProcessedPath path) {
        return path.type().equals(Type.DIRECTORY);
    }

    @NotNull
    private StreamingResponseBody getFileBytes(long id, String requestedPath) {
        return outputStream -> {
            try (InputStream fileStream = minioDAO.downloadFile(requestedPath, id)) {
                fileStream.transferTo(outputStream);
            } catch (Exception e) {
                throw new InternalErrorException("Error during file streaming for path: " + requestedPath, e);
            }
        };
    }

}