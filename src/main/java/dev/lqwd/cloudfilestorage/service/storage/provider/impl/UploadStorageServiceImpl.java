package dev.lqwd.cloudfilestorage.service.storage.provider.impl;

import dev.lqwd.cloudfilestorage.infrastructure.UserDirectoryProvider;
import dev.lqwd.cloudfilestorage.repository.storage.ResourceStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
@RequiredArgsConstructor
public class UploadStorageServiceImpl implements dev.lqwd.cloudfilestorage.service.storage.provider.UploadStorageService {

    private final UserDirectoryProvider userDirectoryProvider;
    private final ResourceStorage resourceStorage;

    public void uploadResource(String path, long id, MultipartFile file) {
        resourceStorage.uploadResource(userDirectoryProvider.provide(path, id), file);
    }

}