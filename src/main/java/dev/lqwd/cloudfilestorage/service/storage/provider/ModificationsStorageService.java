package dev.lqwd.cloudfilestorage.service.storage.provider;

public interface ModificationsStorageService {

    void removeDir(String dirPath, long id);

    void removeFile(String path, long id);

    void moveDir(String from, String to, long id);

    void moveFile(String from, String to, long id);

}
