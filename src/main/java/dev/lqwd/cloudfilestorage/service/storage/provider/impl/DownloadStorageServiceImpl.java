package dev.lqwd.cloudfilestorage.service.storage.provider.impl;

import dev.lqwd.cloudfilestorage.infrastructure.UserDirectoryProvider;
import dev.lqwd.cloudfilestorage.repository.storage.ResourceStorage;
import dev.lqwd.cloudfilestorage.service.storage.provider.DownloadStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.InputStream;


@Service
@RequiredArgsConstructor
public class DownloadStorageServiceImpl implements DownloadStorageService {

    private final UserDirectoryProvider userDirectoryProvider;
    private final ResourceStorage resourceStorage;

    public InputStream downloadFile(String path, long id) {
        return resourceStorage.downloadByPath(userDirectoryProvider.provide(path, id));
    }

    public InputStream downloadFile(String pathWithUserDir) {
        return resourceStorage.downloadByPath(pathWithUserDir);
    }

}