package dev.lqwd.cloudfilestorage.dto.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "multipart")
public record MultipartProperties(
        String maxFileSize,
        String maxRequestSize,
        int maxFilesCount
) {
}
