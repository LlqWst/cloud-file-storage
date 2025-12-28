package dev.lqwd.cloudfilestorage.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class UserDirectoryProvider {

    @Value("${app.minio.root.template.name}")
    private String userRootTemplate;


    public String provide(long id) {
        return userRootTemplate.formatted(id);
    }

    public String provide(String path, long id) {
        return  provide(id) + path;
    }

}
