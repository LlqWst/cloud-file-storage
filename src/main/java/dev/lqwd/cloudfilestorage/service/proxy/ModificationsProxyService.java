package dev.lqwd.cloudfilestorage.service.proxy;

import dev.lqwd.cloudfilestorage.infrastructure.UserDirectoryProvider;
import dev.lqwd.cloudfilestorage.repository.minio.MinioBaseDaoImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@Slf4j
@RequiredArgsConstructor
public class ModificationsProxyService {

    private final UserDirectoryProvider userDirectoryProvider;
    private final MinioBaseDaoImpl minioBaseDao;
    private final FindProxyService findProxyService;


    public void removeDir(String dirPath, long id) {
        findProxyService.findAllResourcesPath(dirPath, id)
                .forEach(minioBaseDao::removeResource);
    }

    public void removeFile(String path, long id) {
        String pathWithUserDir = getPathWithUserDir(path, id);
        minioBaseDao.removeResource(pathWithUserDir);
    }

    public void moveDir(String from, String to, long id) {
        findProxyService.findAllResourcesPath(from, id)
                .forEach(source -> {
                    String target = source.replaceFirst(from, to);
                    minioBaseDao.copyResource(source, target);
                    minioBaseDao.removeResource(source);
                });
    }

    public void moveFile(String from, String to, long id) {
        String fromWithUserDir = getPathWithUserDir(from, id);
        String toWithUserDir = getPathWithUserDir(to, id);
        minioBaseDao.copyResource(fromWithUserDir, toWithUserDir);
        minioBaseDao.removeResource(fromWithUserDir);
    }

    private String getPathWithUserDir(String path, long id) {
        return userDirectoryProvider.provide(path, id);
    }

}