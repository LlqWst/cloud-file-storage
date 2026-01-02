package dev.lqwd.cloudfilestorage.repository.storage;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;


public interface BaseFileStorage {

    InputStream downloadByPath(String pathWithUserDir);

    void createDirectory(String pathWithUserDir);

    void uploadResource(String pathWithUserDir, MultipartFile file);

    void copyResource(String source, String target);

    void removeResource(String pathWithUserDir);

}
