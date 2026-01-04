package dev.lqwd.cloudfilestorage.service.storage.provider.minio;

import dev.lqwd.cloudfilestorage.infrastructure.UserDirectoryProvider;
import dev.lqwd.cloudfilestorage.repository.storage.minio.MinioBaseStorage;
import dev.lqwd.cloudfilestorage.service.storage.provider.ModificationsStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;


@Service
@Slf4j
@RequiredArgsConstructor
public class ModificationsMinioService implements ModificationsStorageService {

    private final UserDirectoryProvider userDirectoryProvider;
    private final MinioBaseStorage minioBaseStorage;
    private final FindMinioService findMinioService;

    public void removeDir(String dirPath, long id) {
        findMinioService.findAllResourcesPath(dirPath, id)
                .forEach(minioBaseStorage::removeResource);
    }

    public void removeFile(String path, long id) {
        String pathWithUserDir = getPathWithUserDir(path, id);
        minioBaseStorage.removeResource(pathWithUserDir);
    }

    public void moveDir(String from, String to, long id) {
        findMinioService.findAllResourcesPath(from, id)
                .forEach(source -> {
                    String target = source.replaceFirst(
                            Pattern.quote(from), to);
                    moveResource(source, target);
                });
    }

    public void moveFile(String from, String to, long id) {
        String fromWithUserDir = getPathWithUserDir(from, id);
        String toWithUserDir = getPathWithUserDir(to, id);
        moveResource(fromWithUserDir, toWithUserDir);
    }

    private void moveResource(String source, String target) {
        minioBaseStorage.copyResource(source, target);
        minioBaseStorage.removeResource(source);
    }

    private String getPathWithUserDir(String path, long id) {
        return userDirectoryProvider.provide(path, id);
    }

}