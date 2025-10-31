package dev.lqwd.cloudfilestorage.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import dev.lqwd.cloudfilestorage.model.Type;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ResourceResponseDTO(
        String path,
        String name,
        Long size,
        Type type
) {
}
