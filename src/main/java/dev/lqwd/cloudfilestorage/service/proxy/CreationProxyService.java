package dev.lqwd.cloudfilestorage.service.proxy;

import dev.lqwd.cloudfilestorage.infrastructure.UserDirectoryProvider;
import dev.lqwd.cloudfilestorage.repository.minio.MinioBaseDaoImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@Slf4j
@RequiredArgsConstructor
public class CreationProxyService {

    private final UserDirectoryProvider userDirectoryProvider;
    private final MinioBaseDaoImpl minioBaseDao;

    public void createDirectory(String path, long id) {
        minioBaseDao.createDirectory(userDirectoryProvider.provide(path, id));
    }

}