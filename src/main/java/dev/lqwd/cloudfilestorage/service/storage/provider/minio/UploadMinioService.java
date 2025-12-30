package dev.lqwd.cloudfilestorage.service.storage.provider.minio;

import dev.lqwd.cloudfilestorage.infrastructure.UserDirectoryProvider;
import dev.lqwd.cloudfilestorage.repository.storage.minio.MinioBaseStorage;
import dev.lqwd.cloudfilestorage.service.storage.provider.UploadStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
@RequiredArgsConstructor
public class UploadMinioService implements UploadStorageService {

    private final UserDirectoryProvider userDirectoryProvider;
    private final MinioBaseStorage minioBaseStorage;

    public void uploadResource(String path, long id, MultipartFile file) {
        minioBaseStorage.uploadResource(userDirectoryProvider.provide(path, id), file);
    }

}