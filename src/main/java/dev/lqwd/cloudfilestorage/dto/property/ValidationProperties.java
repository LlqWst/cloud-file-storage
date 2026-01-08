package dev.lqwd.cloudfilestorage.dto.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Set;

@ConfigurationProperties(prefix = "app.validation")
public record ValidationProperties(
        Set<Character> forbiddenChars,
        int maxLengthPathName
) {
}
