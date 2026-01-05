package dev.lqwd.cloudfilestorage.service.storage.provider.impl;

import dev.lqwd.cloudfilestorage.infrastructure.parser.storage.ParsedResource;
import dev.lqwd.cloudfilestorage.dto.resource.ResourceResponseDto;
import dev.lqwd.cloudfilestorage.exception.NotFoundException;
import dev.lqwd.cloudfilestorage.infrastructure.UserDirectoryProvider;
import dev.lqwd.cloudfilestorage.infrastructure.mapper.ResourceResponseMapper;
import dev.lqwd.cloudfilestorage.infrastructure.parser.storage.StorageResponseParser;
import dev.lqwd.cloudfilestorage.repository.storage.FindAlStorage;
import dev.lqwd.cloudfilestorage.repository.storage.FindStorage;
import dev.lqwd.cloudfilestorage.service.storage.provider.FindStorageService;
import io.minio.StatObjectResponse;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;


@Service
@Slf4j
@RequiredArgsConstructor
public class FindStorageServiceImpl implements FindStorageService {

    private static final String USER_ROOT = "";

    private final UserDirectoryProvider userDirectoryProvider;
    private final FindAlStorage<Item> findAlStorage;
    private final StorageResponseParser<Item> findAllParser;
    private final FindStorage<StatObjectResponse> findStorage;
    private final StorageResponseParser<StatObjectResponse> findParser;
    private final ResourceResponseMapper mapper;

    @Override
    public ResourceResponseDto findResource(String path, long id) {
        String pathWithUserDir = getPathWithUserDir(path, id);
        return findStorage.findResource(pathWithUserDir)
                .map(findParser::pars)
                .map(mapper::toResponseDTO)
                .orElseThrow(() -> new NotFoundException("Resource doesn't exists: " + path));
    }
    
    @Override
    public List<String> findDirResourcesNameWithoutDirRecursive(String path, long id) {
        String pathWithUserDir = getPathWithUserDir(path, id);
        return findAlStorage.findAllResourcePaths(pathWithUserDir, true)
                .stream()
                .filter(excludePath(pathWithUserDir))
                .toList();
    }
    
    @Override
    public List<String> findAllResourcesPath(String path, long id) {
        String userDir = userDirectoryProvider.provide(id);
        String pathWithUserDir = getPathWithUserDir(path, id);
        return findAlStorage.findAllResourcePaths(pathWithUserDir, true)
                .stream()
                .filter(excludePath(userDir))
                .toList();
    }

    @Override
    public List<ResourceResponseDto> findDirResourcesWithoutDir(String path, long id) {
        String pathWithUserDir = getPathWithUserDir(path, id);
        return findAlStorage.findResources(pathWithUserDir, false)
                .stream()
                .map(findAllParser::pars)
                .filter(excludeResource(pathWithUserDir))
                .map(mapper::toResponseDTO)
                .toList();
    }
    
    @Override
    public Stream<ResourceResponseDto> findAllResources(long id) {
        String userDir = userDirectoryProvider.provide(id);
        String pathWithUserDir = getPathWithUserDir(USER_ROOT, id);
        return findAlStorage.findResources(pathWithUserDir, true)
                .stream()
                .map(findAllParser::pars)
                .filter(excludeResource(userDir))
                .map(mapper::toResponseDTO);
    }
    
    @NotNull
    private static Predicate<ParsedResource> excludeResource(String resource) {
        return dto -> !dto.fullPath().equals(resource);
    }

    @NotNull
    private static Predicate<String> excludePath(String pathWithUserDir) {
        return storagePath -> !storagePath.equals(pathWithUserDir);
    }
    
    private String getPathWithUserDir(String path, long id) {
        return userDirectoryProvider.provide(path, id);
    }

}