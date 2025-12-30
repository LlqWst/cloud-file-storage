package dev.lqwd.cloudfilestorage.repository.storage;

import java.util.List;


public interface FindAllResourcesStorage<T> {

    List<T> findResources(String pathWithUserDir, boolean isRecursive);

}
