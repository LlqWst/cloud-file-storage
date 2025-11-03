package dev.lqwd.cloudfilestorage.service.minio;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDirectoryProvider {

    @Value("${app.minio.root.template.name}")
    private String userRootTemplate;

    public String getUserDir(Long id) {
        return userRootTemplate.formatted(id);
    }

    public String getPathWithUserDir(String path, Long id) {
        return getUserDir(id) + path;
    }

}
