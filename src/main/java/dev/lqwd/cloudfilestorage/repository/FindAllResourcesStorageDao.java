package dev.lqwd.cloudfilestorage.repository;

import java.util.List;

public interface FindAllResourcesStorageDao<T> {

    List<T> findResources(String pathWithUserDir, boolean isRecursive);

}
