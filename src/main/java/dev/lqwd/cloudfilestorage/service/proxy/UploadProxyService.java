package dev.lqwd.cloudfilestorage.service.proxy;

import dev.lqwd.cloudfilestorage.infrastructure.UserDirectoryProvider;
import dev.lqwd.cloudfilestorage.repository.minio.MinioBaseDaoImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
@Slf4j
@RequiredArgsConstructor
public class UploadProxyService {

    private final UserDirectoryProvider userDirectoryProvider;
    private final MinioBaseDaoImpl minioBaseDao;


    public void uploadResource(String path, long id, MultipartFile file) {
        minioBaseDao.uploadResource(
                userDirectoryProvider.provide(path, id),
                file);
    }

}