package dev.lqwd.cloudfilestorage.service.storage.provider;

import java.io.InputStream;

public interface DownloadStorageService {

    InputStream downloadFile(String path, long id);

    InputStream downloadFile(String pathWithUserDir);

}
