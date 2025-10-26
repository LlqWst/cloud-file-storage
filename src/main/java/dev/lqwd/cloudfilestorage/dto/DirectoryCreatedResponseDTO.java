package dev.lqwd.cloudfilestorage.dto;

// Пример для файла - folder1/folder2/file.txt
// Пример для папки - folder1/folder2/

import dev.lqwd.cloudfilestorage.model.Type;

public record DirectoryCreatedResponseDTO(
        String path,
        String name,
        Type type
) {
}
