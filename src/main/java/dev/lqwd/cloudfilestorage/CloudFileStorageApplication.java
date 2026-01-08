package dev.lqwd.cloudfilestorage;

import dev.lqwd.cloudfilestorage.dto.property.MultipartProperties;
import dev.lqwd.cloudfilestorage.dto.property.StorageProperties;
import dev.lqwd.cloudfilestorage.dto.property.ValidationProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.retry.annotation.EnableRetry;


@SpringBootApplication
@EnableRetry
@EnableConfigurationProperties({MultipartProperties.class, ValidationProperties.class, StorageProperties.class})
public class CloudFileStorageApplication {

    public static void main(String[] args) {
        SpringApplication.run(CloudFileStorageApplication.class, args);
    }

}
