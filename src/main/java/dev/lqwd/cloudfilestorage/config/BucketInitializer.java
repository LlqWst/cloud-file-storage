package dev.lqwd.cloudfilestorage.config;

import dev.lqwd.cloudfilestorage.repository.storage.minio.MinioBucketStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
@Profile({"prod", "dev", "docker"})
public class BucketInitializer {

    private final MinioBucketStorage bucketStorage;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        bucketStorage.createBucketIfNotExists();
    }

}