package dev.lqwd.cloudfilestorage.config;

import dev.lqwd.cloudfilestorage.infrastructure.storage.BucketStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
@Profile({"prod", "dev", "docker"})
public class BucketInitializer {

    public static final int DELAY_IN_MILLISECOND = 1000;
    private final BucketStorage bucketStorage;

    @EventListener(ApplicationReadyEvent.class)
    @Retryable(backoff = @Backoff(delay = DELAY_IN_MILLISECOND))
    public void onApplicationReady() {
        bucketStorage.createBucketIfNotExists();
    }

    @SuppressWarnings("unused")
    @Recover
    public void recover(Exception e) {
        throw new IllegalStateException("Failed to initialize MinIO bucket after retries", e);
    }

}