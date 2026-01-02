package dev.lqwd.cloudfilestorage.repository.storage;


public interface StorageBucket {

    void createBucketIfNotExists();

    boolean isBucketExists();

    void createRootBucket();

}
