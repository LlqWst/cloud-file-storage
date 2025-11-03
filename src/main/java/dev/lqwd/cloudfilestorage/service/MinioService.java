package dev.lqwd.cloudfilestorage.service;

import dev.lqwd.cloudfilestorage.dto.resource.ResourceResponseDTO;
import dev.lqwd.cloudfilestorage.exception.AlreadyExistException;
import dev.lqwd.cloudfilestorage.exception.NotFoundException;
import dev.lqwd.cloudfilestorage.repository.minio.MinioDAO;
import dev.lqwd.cloudfilestorage.utils.UserDirectoryProvider;
import dev.lqwd.cloudfilestorage.utils.path_processor.ProcessedPath;
import dev.lqwd.cloudfilestorage.utils.parser.ItemParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class MinioService {

    private final UserDirectoryProvider userDirectoryProvider;
    private final ItemParser itemParser;
    private final MinioDAO minioDAO;

    public void createUserRootDir(long id) {
        String userDir = userDirectoryProvider.provide(id);
        if (!minioDAO.isDirExist(userDir)) {
            minioDAO.createDirectory(userDir);
        }
    }

    public void createNewDir(ProcessedPath path, long id) {
        String fullPath = userDirectoryProvider.provide(path.requestedPath(), id);
        String parentPath = userDirectoryProvider.provide(path.parentPath(), id);
        if (minioDAO.isExist(fullPath, parentPath)) {
            throw new AlreadyExistException("Resource already exists: " + fullPath);
        }
        minioDAO.createDirectory(fullPath);
    }

    public ResourceResponseDTO getResource(ProcessedPath path, long id) {
        String fullPath = userDirectoryProvider.provide(path.requestedPath(), id);
        String parentPath = userDirectoryProvider.provide(path.parentPath(), id);
        return minioDAO.findResource(fullPath, parentPath)
                .map(itemParser::pars)
                .orElseThrow(() -> new NotFoundException("Resource doesn't exist: " + fullPath));
    }

    public List<ResourceResponseDTO> getResources(ProcessedPath path, long id) {
        String fullPath = userDirectoryProvider.provide(path.requestedPath(), id);
        return minioDAO.findDirectoryResources(fullPath).stream()
                .map(itemParser::pars)
                .toList();
    }

    public void createFile(ProcessedPath path, long id) {
        String fullPath = userDirectoryProvider.provide(path.requestedPath(), id);
        String parentPath = userDirectoryProvider.provide(path.parentPath(), id);
        if (minioDAO.isExist(fullPath, parentPath)) {
            throw new AlreadyExistException("Resource already exists: " + fullPath);
        }
        minioDAO.buildFile(fullPath);
    }

}