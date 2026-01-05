package dev.lqwd.cloudfilestorage.repository.storage;

import java.util.List;


public interface FindAlStorage<T> {

    List<T> findResources(String pathWithUserDir, boolean isRecursive);

    List<String> findAllResourcePaths(String pathWithUserDir, boolean isRecursive);

}
