package dev.lqwd.cloudfilestorage.service.storage.provider.minio;

import dev.lqwd.cloudfilestorage.infrastructure.UserDirectoryProvider;
import dev.lqwd.cloudfilestorage.repository.storage.minio.MinioBaseStorage;
import dev.lqwd.cloudfilestorage.service.storage.provider.CreationStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class CreationMinioService implements CreationStorageService {

    private final UserDirectoryProvider userDirectoryProvider;
    private final MinioBaseStorage minioBaseStorage;

    public void createDirectory(String path, long id) {
        minioBaseStorage.createDirectory(userDirectoryProvider.provide(path, id));
    }

}