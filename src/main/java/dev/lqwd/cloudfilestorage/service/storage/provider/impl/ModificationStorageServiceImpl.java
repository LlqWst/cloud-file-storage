package dev.lqwd.cloudfilestorage.service.storage.provider.impl;

import dev.lqwd.cloudfilestorage.infrastructure.UserDirectoryProvider;
import dev.lqwd.cloudfilestorage.repository.storage.ResourceStorage;
import dev.lqwd.cloudfilestorage.service.storage.provider.ModificationStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;


@Service
@Slf4j
@RequiredArgsConstructor
public class ModificationStorageServiceImpl implements ModificationStorageService {

    private final UserDirectoryProvider userDirectoryProvider;
    private final ResourceStorage resourceStorage;
    private final FindStorageServiceImpl findService;

    @Override
    public void removeDir(String dirPath, long id) {
        findService.findAllResourcesPath(dirPath, id)
                .forEach(resourceStorage::removeResource);
    }

    @Override
    public void removeFile(String path, long id) {
        String pathWithUserDir = getPathWithUserDir(path, id);
        resourceStorage.removeResource(pathWithUserDir);
    }

    @Override
    public void moveDir(String from, String to, long id) {
        findService.findAllResourcesPath(from, id)
                .forEach(source -> {
                    String target = source.replaceFirst(
                            Pattern.quote(from), to);
                    moveResource(source, target);
                });
    }

    @Override
    public void moveFile(String from, String to, long id) {
        String fromWithUserDir = getPathWithUserDir(from, id);
        String toWithUserDir = getPathWithUserDir(to, id);
        moveResource(fromWithUserDir, toWithUserDir);
    }

    private void moveResource(String source, String target) {
        resourceStorage.copyResource(source, target);
        resourceStorage.removeResource(source);
    }

    private String getPathWithUserDir(String path, long id) {
        return userDirectoryProvider.provide(path, id);
    }

}