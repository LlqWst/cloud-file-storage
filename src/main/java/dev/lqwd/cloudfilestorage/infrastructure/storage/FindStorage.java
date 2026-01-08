package dev.lqwd.cloudfilestorage.infrastructure.storage;

import java.util.Optional;


public interface FindStorage<T>{

    Optional<T> findResource(String pathWithUserDir);

}
