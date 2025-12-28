package dev.lqwd.cloudfilestorage.repository;

import java.util.Optional;

public interface FindResourceStorageDao <T>{

    Optional<T> findResource(String pathWithUserDir);

}
