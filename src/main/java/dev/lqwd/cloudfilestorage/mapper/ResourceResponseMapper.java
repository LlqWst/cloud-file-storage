package dev.lqwd.cloudfilestorage.mapper;

import dev.lqwd.cloudfilestorage.dto.resource.DirectoryResourceDTO;
import dev.lqwd.cloudfilestorage.dto.resource.FileResourceDTO;
import dev.lqwd.cloudfilestorage.utils.path_processor.ProcessedPath;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ResourceResponseMapper {


    @Mapping(source = "parentPath", target = "path")
    @Mapping(source = "resourceName", target = "name")
    @Mapping(source = "type", target = "type")
    DirectoryResourceDTO toDirResponseDTO(ProcessedPath path);

    @Mapping(source = "parentPath", target = "path")
    @Mapping(source = "resourceName", target = "name")
    @Mapping(source = "type", target = "type")
    FileResourceDTO toFileResponseDTO(ProcessedPath path);
}
