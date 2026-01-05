package dev.lqwd.cloudfilestorage.repository.storage;

import java.util.Optional;


public interface FindStorage<T>{

    Optional<T> findResource(String pathWithUserDir);

}
