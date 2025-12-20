package dev.lqwd.cloudfilestorage.mapper;

import dev.lqwd.cloudfilestorage.dto.resource.DirectoryResponseDto;
import dev.lqwd.cloudfilestorage.dto.resource.FileResponseDto;
import dev.lqwd.cloudfilestorage.dto.resource.ResourceResponseDto;
import dev.lqwd.cloudfilestorage.exception.InternalErrorException;
import dev.lqwd.cloudfilestorage.utils.path_processor.ProcessedPath;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ResourceResponseMapper {


    @Mapping(source = "parentPath", target = "path")
    @Mapping(source = "resourceName", target = "name")
    @Mapping(source = "type", target = "type")
    DirectoryResponseDto toDirResponseDTO(ProcessedPath path);

    @Mapping(source = "path.parentPath", target = "path")
    @Mapping(source = "path.resourceName", target = "name")
    @Mapping(source = "path.type", target = "type")
    @Mapping(source = "size", target = "size")
    FileResponseDto toFileResponseDTO(ProcessedPath path, long size);

    default ResourceResponseDto toResponseDTO(ProcessedPath path, long size) {

        switch (path.type()) {
            case DIRECTORY -> {
                return toDirResponseDTO(path);
            }
            case FILE -> {
                return toFileResponseDTO(path, size);
            }
            default -> throw new InternalErrorException("incorrect type");
        }
    }
}
