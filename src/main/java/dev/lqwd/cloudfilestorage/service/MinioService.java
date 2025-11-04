package dev.lqwd.cloudfilestorage.service;

import dev.lqwd.cloudfilestorage.dto.resource.ResourceResponseDTO;
import dev.lqwd.cloudfilestorage.entity.Type;
import dev.lqwd.cloudfilestorage.exception.NotFoundException;
import dev.lqwd.cloudfilestorage.repository.minio.MinioDAO;
import dev.lqwd.cloudfilestorage.utils.parser.minio.ItemParser;
import dev.lqwd.cloudfilestorage.utils.path_processor.ProcessedPath;
import dev.lqwd.cloudfilestorage.utils.parser.minio.StatObjectParser;
import io.minio.StatObjectResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class MinioService {

    public static final String EMPTY = "";

    private final StatObjectParser statObjectParser;
    private final ItemParser itemParser;
    private final MinioDAO minioDAO;

    public void createUserRootDir(long id) {
        if (!minioDAO.isExist(EMPTY, id)) {
            minioDAO.createDirectory(EMPTY, id);
        }
    }

    public void createNewDir(ProcessedPath path, long id) {
        String parentPath = path.parentPath();
        if (!minioDAO.isExist(parentPath, id)) {
            throw new NotFoundException("Parent path doesn't exist: " + parentPath);
        }
        minioDAO.createDirectory(path.requestedPath(), id);
    }

    public ResourceResponseDTO getResource(ProcessedPath path, long id) {
        StatObjectResponse statObject = minioDAO.findResource(path.requestedPath(), id);
        return statObjectParser.pars(statObject);
    }

    public List<ResourceResponseDTO> getResources(ProcessedPath path, long id) {
        return minioDAO.findDirectoryResourcesWithoutDir(path.requestedPath(), id).stream()
                .map(itemParser::pars)
                .toList();
    }

    public void removeResource(ProcessedPath path, long id) {
        String requestedPath = path.requestedPath();
        if(path.type().equals(Type.DIRECTORY)) {
            minioDAO.removeDir(requestedPath, id);
        } else {
            minioDAO.removeFile(requestedPath, id);
        }
    }

    public void createFile(ProcessedPath path, long id) {
        minioDAO.buildFile(path.requestedPath(), id);
    }

}