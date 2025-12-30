package dev.lqwd.cloudfilestorage.service.storage.provider;

import dev.lqwd.cloudfilestorage.dto.resource.ResourceResponseDto;

import java.util.List;
import java.util.stream.Stream;

public interface FindStorageService {

    ResourceResponseDto findResource(String path, long id);

    List<ResourceResponseDto> findDirResourcesWithoutDir(String path, long id);

    List<String> findDirResourcesNameWithoutDirRecursive(String path, long id);

    List<String> findAllResourcesPath(String path, long id);

    Stream<ResourceResponseDto> findAllResources(long id);

}
