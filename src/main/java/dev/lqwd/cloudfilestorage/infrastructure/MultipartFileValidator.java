package dev.lqwd.cloudfilestorage.infrastructure;

import dev.lqwd.cloudfilestorage.exception.BadRequestException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;


@Component
public class MultipartFileValidator {

    public void validate(MultipartFile[] files) {
        validateOnEmpty(files);
        validateFileName(files);
    }

    private static void validateOnEmpty(MultipartFile[] files) {
        if (files == null || files.length == 0) {
            throw new BadRequestException("No files provided");
        }
    }

    private static void validateFileName(MultipartFile[] files) {
        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                throw new BadRequestException("File is empty");
            }
            String filename = file.getOriginalFilename();
            if (filename == null || filename.isBlank()) {
                throw new BadRequestException("File bucketName is missing");
            }
        }
    }

}
