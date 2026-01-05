package dev.lqwd.cloudfilestorage.service.storage.provider.impl;

import dev.lqwd.cloudfilestorage.infrastructure.UserDirectoryProvider;
import dev.lqwd.cloudfilestorage.repository.storage.ResourceStorage;
import dev.lqwd.cloudfilestorage.service.storage.provider.CreationStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class CreationStorageServiceImpl implements CreationStorageService {

    private final UserDirectoryProvider userDirectoryProvider;
    private final ResourceStorage resourceStorage;

    public void createDirectory(String path, long id) {
        resourceStorage.createDirectory(userDirectoryProvider.provide(path, id));
    }

}