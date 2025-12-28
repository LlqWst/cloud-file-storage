package dev.lqwd.cloudfilestorage.service.proxy;

import dev.lqwd.cloudfilestorage.infrastructure.UserDirectoryProvider;
import dev.lqwd.cloudfilestorage.repository.minio.MinioBaseDaoImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream;


@Service
@Slf4j
@RequiredArgsConstructor
public class DownloadProxyService {

    private final UserDirectoryProvider userDirectoryProvider;
    private final MinioBaseDaoImpl minioBaseDao;


    public InputStream downloadFile(String path, long id) {
        return minioBaseDao.downloadByPath(userDirectoryProvider.provide(path, id));
    }

    public InputStream downloadFile(String pathWithUserDir) {
        return minioBaseDao.downloadByPath(pathWithUserDir);
    }

}