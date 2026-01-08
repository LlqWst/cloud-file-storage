package dev.lqwd.cloudfilestorage.dto.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.storage")
public record StorageProperties(
        String bucketName,
        String rootTemplate
) {
}
