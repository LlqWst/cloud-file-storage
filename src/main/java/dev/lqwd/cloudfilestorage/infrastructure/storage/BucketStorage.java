package dev.lqwd.cloudfilestorage.infrastructure.storage;


public interface BucketStorage {

    void createBucketIfNotExists();

    boolean isBucketExists();

    void createRootBucket();

}
