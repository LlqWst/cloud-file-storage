package dev.lqwd.cloudfilestorage.repository.storage;

import java.util.Optional;


public interface FindResourceStorage<T>{

    Optional<T> findResource(String pathWithUserDir);

}
