package dev.lqwd.cloudfilestorage.repository.minio;

import dev.lqwd.cloudfilestorage.exception.AlreadyExistException;
import dev.lqwd.cloudfilestorage.exception.NotFoundException;
import dev.lqwd.cloudfilestorage.utils.PathNormalizer;
import dev.lqwd.cloudfilestorage.utils.UserDirectoryProvider;
import io.minio.StatObjectResponse;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;


@Component
@Slf4j
@RequiredArgsConstructor
public class MinioServiceRep {

    private static final String SLASH = "/";
    private static final String ROOT = "";

    private final PathNormalizer pathNormalizer;
    private final UserDirectoryProvider userDirectoryProvider;
    private final MinioDao minioDao;


    public StatObjectResponse findResource(String path, long id) {
        return minioDao.findResource(getPathWithUserDir(path, id))
                .orElseThrow(() -> new NotFoundException("Resource doesn't exists: " + path));
    }

    public void createDirectory(String path, long id) {
        minioDao.createDirectory(getPathWithUserDir(path, id));
    }

    public void uploadResource(String path, long id, MultipartFile file) {
        minioDao.uploadResource(getPathWithUserDir(path, id), file);
    }

    private String getPathWithUserDir(String path, long id) {
        return userDirectoryProvider.provide(path, id);
    }

    public boolean isExistIgnoreEndSlash(String path, long id) {
        String pathWithoutEndSlash = pathNormalizer.getPathWithoutEndSlash(path);
        String pathWithEndSlash = pathWithoutEndSlash + SLASH;
        return isExist(pathWithoutEndSlash, id) || isExist(pathWithEndSlash, id);
    }

    public boolean isExist(String path, long id) {
        return minioDao.findResource(getPathWithUserDir(path, id))
                .isPresent();
    }

    public List<Item> findDirResourcesWithoutDir(String path, long id) {
        return getItems(getPathWithUserDir(path, id), false);
    }

    public List<Item> findDirResourcesWithoutDirRecursive(String path, long id) {
        return getItems(getPathWithUserDir(path, id), true);
    }

    private List<Item> findDirResourcesWithDirRecursive(String path, long id) {
        String userDir = userDirectoryProvider.provide(id);
        String pathWithUserDir = getPathWithUserDir(path, id);
        return minioDao.findResources(pathWithUserDir, true)
                .stream()
                .filter(item -> !item.objectName().equals(userDir))
                .toList();
    }

    private List<String> findAllResourcesPath(String path, long id) {
        return findDirResourcesWithDirRecursive(path, id)
                .stream()
                .map(Item::objectName)
                .toList();
    }

    public void removeDir(String dirPath, long id) {
        findAllResourcesPath(dirPath, id)
                .forEach(minioDao::removeResource);
    }

    public void removeFile(String path, long id) {
        String pathWithUserDir = getPathWithUserDir(path, id);
        minioDao.removeResource(pathWithUserDir);
    }

    public void moveDir(String from, String to, long id) {
        findAllResourcesPath(from, id)
                .forEach(source -> {
                    String target = source.replaceFirst(from, to);
                    minioDao.copyResource(source, target);
                    minioDao.removeResource(source);
                });
    }

    public void moveFile(String from, String to, long id) {
        String fromWithUserDir = getPathWithUserDir(from, id);
        String toWithUserDir = getPathWithUserDir(to, id);
        minioDao.copyResource(fromWithUserDir, toWithUserDir);
        minioDao.removeResource(fromWithUserDir);
    }

    public void validateOnExistence(String path, long id) {
        if (isExistIgnoreEndSlash(path, id)) {
            throw new AlreadyExistException("Resource already exists: " + path);
        }
    }

    public void validateOnAbsence(String path, long id) {
        if (!isExist(path, id)) {
            throw new NotFoundException("Resource doesn't exists: " + path);
        }
    }

    public InputStream downloadFile(String path, long id) {
        String pathWithUserDir = getPathWithUserDir(path, id);
        return minioDao.downloadByPath(pathWithUserDir);
    }

    public InputStream downloadFile(String path) {
        return minioDao.downloadByPath(path);
    }

    public List<Item> findAllResources(long id) {
        return findDirResourcesWithDirRecursive(ROOT, id);
    }

    private List<Item> getItems(String pathWithUserDir, boolean isRecursive) {
        return minioDao.findResources(pathWithUserDir, isRecursive)
                .stream()
                .filter(item -> !item.objectName().equals(pathWithUserDir))
                .toList();
    }

}