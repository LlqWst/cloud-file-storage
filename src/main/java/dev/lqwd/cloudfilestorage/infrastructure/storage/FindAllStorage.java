package dev.lqwd.cloudfilestorage.infrastructure.storage;

import java.util.List;


public interface FindAllStorage<T> {

    List<T> findResources(String pathWithUserDir, boolean isRecursive);

    List<String> findAllResourcePaths(String pathWithUserDir, boolean isRecursive);

}
