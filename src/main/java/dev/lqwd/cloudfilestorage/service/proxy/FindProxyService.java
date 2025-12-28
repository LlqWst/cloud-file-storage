package dev.lqwd.cloudfilestorage.service.proxy;

import dev.lqwd.cloudfilestorage.exception.NotFoundException;
import dev.lqwd.cloudfilestorage.infrastructure.UserDirectoryProvider;
import dev.lqwd.cloudfilestorage.repository.minio.MinioFindAllImpl;
import dev.lqwd.cloudfilestorage.repository.minio.MinioFindImpl;
import io.minio.StatObjectResponse;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class FindProxyService {

    private static final String ROOT = "";

    private final UserDirectoryProvider userDirectoryProvider;
    private final MinioFindAllImpl minioFindAllDao;
    private final MinioFindImpl minioFindDao;


    public StatObjectResponse findResource(String path, long id) {
        return minioFindDao.findResource(getPathWithUserDir(path, id))
                .orElseThrow(() -> new NotFoundException("Resource doesn't exists: " + path));
    }

    public List<Item> findDirResourcesWithoutDir(String path, long id) {
        return getItems(getPathWithUserDir(path, id), false);
    }

    public List<Item> findDirResourcesWithoutDirRecursive(String path, long id) {
        return getItems(getPathWithUserDir(path, id), true);
    }

    public List<Item> findDirResourcesWithDirRecursive(String path, long id) {
        String userDir = userDirectoryProvider.provide(id);
        String pathWithUserDir = getPathWithUserDir(path, id);

        return minioFindAllDao.findResources(pathWithUserDir, true)
                .stream()
                .filter(item -> !item.objectName().equals(userDir))
                .toList();
    }

    public List<String> findAllResourcesPath(String path, long id) {
        return findDirResourcesWithDirRecursive(path, id)
                .stream()
                .map(Item::objectName)
                .toList();
    }

    public List<Item> findAllResources(long id) {
        return findDirResourcesWithDirRecursive(ROOT, id);
    }

    private List<Item> getItems(String pathWithUserDir, boolean isRecursive) {
        return minioFindAllDao.findResources(pathWithUserDir, isRecursive)
                .stream()
                .filter(item -> !item.objectName().equals(pathWithUserDir))
                .toList();
    }

    private String getPathWithUserDir(String path, long id) {
        return userDirectoryProvider.provide(path, id);
    }

}