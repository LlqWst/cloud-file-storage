package dev.lqwd.cloudfilestorage.service;

import dev.lqwd.cloudfilestorage.dto.resource.ResourceResponseDTO;
import dev.lqwd.cloudfilestorage.entity.Type;
import dev.lqwd.cloudfilestorage.exception.BadRequestException;
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
        String requestedPath = getRequestedPath(path);
        validatePArentPath(id, parentPath);
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
        return minioDAO.findDirectoryResourcesWithoutDir(requestedPath, id).stream()
                .map(itemParser::pars)
                .toList();
    }

    public void removeResource(ProcessedPath path, long id) {
        String requestedPath = getRequestedPath(path);
        minioDAO.validateOnAbsence(requestedPath, id);
        if(path.type().equals(Type.DIRECTORY)) {
            minioDAO.removeDir(requestedPath, id);
        } else {
            minioDAO.removeFile(requestedPath, id);
        }
    }

    public void moveResource(ProcessedPath pathFrom, ProcessedPath pathTo, long id) {
        if (!pathFrom.type().equals(pathTo.type())){
            throw new BadRequestException("The resource types must match");
        }
        String from = getRequestedPath(pathFrom);
        String to = getRequestedPath(pathTo);

        if (from.equals("/") || from.isBlank()){
            throw new BadRequestException("You can't cut the root directory");
        }

        String parentPathTo = pathTo.parentPath();
        validatePArentPath(id, parentPathTo);

        minioDAO.validateOnAbsence(from, id);
        minioDAO.validateOnExistence(to, id);

        if(pathFrom.type().equals(Type.DIRECTORY)) {
            minioDAO.moveDir(from, to, id);
        } else {
            minioDAO.moveFile(from, to, id);
        }
    }

    private void validatePArentPath(long id, String parentPathTo) {
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
        return minioDAO.findAllResources(id).stream()
                .map(itemParser::pars)
                .filter(dto -> dto.name()
                        .toLowerCase()
                        .contains(lowerCaseQuery))
                .toList();
    }

    private static String getRequestedPath(ProcessedPath path) {
        return path.requestedPath();
    }

}