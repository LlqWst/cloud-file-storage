package dev.lqwd.cloudfilestorage.service.storage.provider;

import org.springframework.web.multipart.MultipartFile;

public interface UploadStorageService {

    void uploadResource(String path, long id, MultipartFile file);

}
