package dev.lqwd.cloudfilestorage.service.storage.provider.minio;

import dev.lqwd.cloudfilestorage.dto.resource.ResourceResponseDto;
import dev.lqwd.cloudfilestorage.exception.NotFoundException;
import dev.lqwd.cloudfilestorage.infrastructure.UserDirectoryProvider;
import dev.lqwd.cloudfilestorage.parser.minio.ItemParser;
import dev.lqwd.cloudfilestorage.parser.minio.StatObjectParser;
import dev.lqwd.cloudfilestorage.repository.storage.minio.MinioFindAllStorage;
import dev.lqwd.cloudfilestorage.repository.storage.minio.MinioFindStorage;
import dev.lqwd.cloudfilestorage.service.storage.provider.FindStorageService;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;


@Service
@Slf4j
@RequiredArgsConstructor
public class FindMinioService implements FindStorageService {

    private static final String USER_ROOT = "";

    private final UserDirectoryProvider userDirectoryProvider;
    private final MinioFindAllStorage minioFindAllStorage;
    private final MinioFindStorage minioFindStorage;
    private final StatObjectParser statObjectParser;
    private final ItemParser itemParser;

    public ResourceResponseDto findResource(String path, long id) {
        return minioFindStorage.findResource(getPathWithUserDir(path, id))
                .map(statObjectParser::pars)
                .orElseThrow(() -> new NotFoundException("Resource doesn't exists: " + path));
    }

    public List<ResourceResponseDto> findDirResourcesWithoutDir(String path, long id) {
        return getItems(getPathWithUserDir(path, id), false)
                .stream()
                .map(itemParser::pars)
                .toList();
    }

    public List<String> findDirResourcesNameWithoutDirRecursive(String path, long id) {
        return getItems(getPathWithUserDir(path, id), true)
                .stream()
                .map(Item::objectName)
                .toList();
    }

    public List<String> findAllResourcesPath(String path, long id) {
        return findDirResourcesWithDirRecursive(path, id)
                .map(Item::objectName)
                .toList();
    }

    public Stream<ResourceResponseDto> findAllResources(long id) {
        return findDirResourcesWithDirRecursive(USER_ROOT, id)
                .map(itemParser::pars);
    }

    private Stream<Item> findDirResourcesWithDirRecursive(String path, long id) {
        String userDir = userDirectoryProvider.provide(id);
        String pathWithUserDir = getPathWithUserDir(path, id);
        return minioFindAllStorage.findResources(pathWithUserDir, true)
                .stream()
                .filter(item -> !item.objectName().equals(userDir));
    }

    private List<Item> getItems(String pathWithUserDir, boolean isRecursive) {
        return minioFindAllStorage.findResources(pathWithUserDir, isRecursive)
                .stream()
                .filter(item -> !item.objectName().equals(pathWithUserDir))
                .toList();
    }

    private String getPathWithUserDir(String path, long id) {
        return userDirectoryProvider.provide(path, id);
    }

}