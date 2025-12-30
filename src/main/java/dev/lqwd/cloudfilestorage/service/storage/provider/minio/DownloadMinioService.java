package dev.lqwd.cloudfilestorage.service.storage.provider.minio;

import dev.lqwd.cloudfilestorage.infrastructure.UserDirectoryProvider;
import dev.lqwd.cloudfilestorage.repository.storage.minio.MinioBaseStorage;
import dev.lqwd.cloudfilestorage.service.storage.provider.DownloadStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.InputStream;


@Service
@RequiredArgsConstructor
public class DownloadMinioService implements DownloadStorageService {

    private final UserDirectoryProvider userDirectoryProvider;
    private final MinioBaseStorage minioBaseStorage;

    public InputStream downloadFile(String path, long id) {
        return minioBaseStorage.downloadByPath(userDirectoryProvider.provide(path, id));
    }

    public InputStream downloadFile(String pathWithUserDir) {
        return minioBaseStorage.downloadByPath(pathWithUserDir);
    }

}