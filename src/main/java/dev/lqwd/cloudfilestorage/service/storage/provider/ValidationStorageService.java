package dev.lqwd.cloudfilestorage.service.storage.provider;


public interface ValidationStorageService {

    boolean isExistIgnoreEndSlash(String path, long id);

    boolean isExist(String path, long id);

    void validateOnExistence(String path, long id);

    void validateOnAbsence(String path, long id);

    void validateParentPath(long id, String parentPathTo);

}
