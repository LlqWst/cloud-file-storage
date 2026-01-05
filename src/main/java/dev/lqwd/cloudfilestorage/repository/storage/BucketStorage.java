package dev.lqwd.cloudfilestorage.repository.storage;


public interface BucketStorage {

    void createBucketIfNotExists();

    boolean isBucketExists();

    void createRootBucket();

}
