package dev.lqwd.cloudfilestorage.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class UserDirectoryProvider {

    @Value("${app.minio.root.template.name}")
    private String userRootTemplate;

    private final PathNormalizer normalizer;

    public String provide(Long id) {
        return userRootTemplate.formatted(id);
    }

    public String provide(String path, long id) {
        return  normalizer.normalize(provide(id) + path);
    }

}
