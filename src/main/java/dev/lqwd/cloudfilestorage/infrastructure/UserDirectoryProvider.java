package dev.lqwd.cloudfilestorage.infrastructure;

import dev.lqwd.cloudfilestorage.dto.property.StorageProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class UserDirectoryProvider {

    private final StorageProperties storageProperties;

    public String provide(long id) {
        return storageProperties.rootTemplate().formatted(id);
    }

    public String provide(String path, long id) {
        return  provide(id) + path;
    }

}
