package dev.lqwd.cloudfilestorage.service;

import dev.lqwd.cloudfilestorage.dto.resource.ResourceResponseDTO;
import dev.lqwd.cloudfilestorage.entity.Type;
import dev.lqwd.cloudfilestorage.exception.BadRequestException;
import dev.lqwd.cloudfilestorage.exception.InternalErrorException;
import dev.lqwd.cloudfilestorage.exception.NotFoundException;
import dev.lqwd.cloudfilestorage.mapper.ResourceResponseMapper;
import dev.lqwd.cloudfilestorage.repository.minio.MinioDAO;
import dev.lqwd.cloudfilestorage.utils.UserDirectoryProvider;
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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


@Service
@Slf4j
@RequiredArgsConstructor
public class MinioService {

    private static final String EMPTY = "";
    private static final String ROOT_DIR = "/";

    private final StatObjectParser statObjectParser;
    private final ItemParser itemParser;
    private final MinioDAO minioDAO;
    private final UserDirectoryProvider userDirectoryProvider;
    private final PathProcessor pathProcessor;
    private final ResourceResponseMapper mapper;

    public void createUserRootDir(long id) {
        if (!minioDAO.isExist(EMPTY, id)) {
            minioDAO.createDirectory(EMPTY, id);
        }
    }

    public void createNewDir(ProcessedPath path, long id) {
        String parentPath = path.parentPath();
        String requestedPath = getRequestedPath(path);
        validateParentPath(id, parentPath);
        minioDAO.validateOnExistence(requestedPath, id);
        minioDAO.createDirectory(requestedPath, id);
    }

    public ResourceResponseDTO getResource(ProcessedPath path, long id) {
        StatObjectResponse statObject = minioDAO.findResource(getRequestedPath(path), id);
        return statObjectParser.pars(statObject);
    }

    public List<ResourceResponseDTO> getResources(ProcessedPath path, long id) {
        String requestedPath = getRequestedPath(path);
        minioDAO.validateOnAbsence(requestedPath, id);
        return minioDAO.findDirectoryResourcesWithoutDir(requestedPath, id)
                .stream()
                .map(itemParser::pars)
                .toList();
    }

    public void removeResource(ProcessedPath path, long id) {
        String requestedPath = getRequestedPath(path);
        minioDAO.validateOnAbsence(requestedPath, id);
        if (isDirectory(path)) {
            minioDAO.removeDir(requestedPath, id);
        } else {
            minioDAO.removeFile(requestedPath, id);
        }
    }

    public void moveResource(ProcessedPath pathFrom, ProcessedPath pathTo, long id) {
        if (!pathFrom.type().equals(pathTo.type())) {
            throw new BadRequestException("The resource types must match");
        }
        String from = getRequestedPath(pathFrom);
        String to = getRequestedPath(pathTo);

        if (from.equals(ROOT_DIR) || from.isBlank()) {
            throw new BadRequestException("You can't cut the root directory");
        }

        String parentPathTo = pathTo.parentPath();
        validateParentPath(id, parentPathTo);

        minioDAO.validateOnAbsence(from, id);
        minioDAO.validateOnExistence(to, id);

        if (isDirectory(pathFrom)) {
            minioDAO.moveDir(from, to, id);
        } else {
            minioDAO.moveFile(from, to, id);
        }
    }

    private void validateParentPath(long id, String parentPathTo) {
        if (!minioDAO.isExist(parentPathTo, id)) {
            throw new NotFoundException("Parent path doesn't exist: " + parentPathTo);
        }
    }

    public void createFile(ProcessedPath path, long id) {
        minioDAO.validateOnExistence(getRequestedPath(path), id);
        minioDAO.buildFile(getRequestedPath(path), id);
    }

    public List<ResourceResponseDTO> searchResource(String query, long id) {
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

    public StreamingResponseBody download(ProcessedPath path, long id) {
        String requestedPath = path.requestedPath();
        minioDAO.validateOnAbsence(requestedPath, id);

        if (isDirectory(path)) {
            String userDir = userDirectoryProvider.provide(id);
            return outputStream -> {
                try (ZipOutputStream zipOut = new ZipOutputStream(outputStream)) {
                    List<Item> resources = minioDAO.findDirectoryResourcesWithoutDirRecursive(requestedPath, id);
                    for (Item resource : resources) {
                        String resourcePath = resource.objectName();
                        String entryName = resourcePath.replaceFirst(userDir, EMPTY);
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
            };
        } else {
            return getFileBytes(id, requestedPath);
        }
    }

    public List<ResourceResponseDTO> upload(ProcessedPath folderPath, long id, MultipartFile[] files){
        String requestedFolderPath = folderPath.requestedPath();
        minioDAO.validateOnAbsence(requestedFolderPath, id);
        for(MultipartFile file : files){
            minioDAO.validateOnExistence(file.getOriginalFilename(), id);
        }
        List<ResourceResponseDTO> resources = new ArrayList<>();
        for(MultipartFile file : files){
            String filePath = file.getOriginalFilename();
            ProcessedPath processed = pathProcessor.processDir(filePath);
            minioDAO.uploadResource(requestedFolderPath, id, file);
            ResourceResponseDTO responseDTO = mapper.toResponseDTO(processed, file.getSize());
            resources.add(responseDTO);
        }
        return resources;
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