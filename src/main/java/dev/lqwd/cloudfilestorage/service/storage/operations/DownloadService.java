package dev.lqwd.cloudfilestorage.service.storage.operations;

import dev.lqwd.cloudfilestorage.dto.resource.DownloadedResponseDto;
import dev.lqwd.cloudfilestorage.entity.Type;
import dev.lqwd.cloudfilestorage.exception.InternalErrorException;
import dev.lqwd.cloudfilestorage.service.storage.provider.minio.DownloadMinioService;
import dev.lqwd.cloudfilestorage.service.storage.provider.minio.FindMinioService;
import dev.lqwd.cloudfilestorage.path_processor.PathProcessor;
import dev.lqwd.cloudfilestorage.path_processor.ProcessedPath;
import dev.lqwd.cloudfilestorage.service.storage.provider.minio.ValidationMinioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


@Component
@RequiredArgsConstructor
public class DownloadService {

    private static final String ZIP_EXTENSION = ".zip";
    private static final String SLASH = "/";

    private final PathProcessor pathProcessor;
    private final DownloadMinioService downloadStorageService;
    private final ValidationMinioService validationStorageService;
    private final FindMinioService findMinioService;


    public DownloadedResponseDto download(String rawPath, long id) {
        ProcessedPath path = pathProcessor.processResource(rawPath);
        String requestedPath = path.requestedPath();
        validationStorageService.validateOnAbsence(requestedPath, id);

        if (isDirectory(path)) {
            return DownloadedResponseDto.builder()
                    .content(getZipBytes(id, requestedPath, path))
                    .name(path.resourceName() + ZIP_EXTENSION)
                    .build();
        }
        return DownloadedResponseDto.builder()
                .content(getFileBytes(id, requestedPath))
                .name(path.resourceName())
                .build();
    }

    private StreamingResponseBody getFileBytes(long id, String requestedPath) {
        return outputStream -> {
            try (InputStream fileStream = downloadStorageService.downloadFile(requestedPath, id)) {
                fileStream.transferTo(outputStream);
            } catch (Exception e) {
                throw new InternalErrorException("Error during file streaming for path: " + requestedPath, e);
            }
        };
    }

    private StreamingResponseBody getZipBytes(long id, String requestedPath, ProcessedPath path) {
        return outputStream -> toZip(id, outputStream, requestedPath, path);
    }

    private void toZip(long id, OutputStream outputStream, String requestedPath, ProcessedPath path) {
        try (ZipOutputStream zipOut = new ZipOutputStream(outputStream)) {
            findMinioService.findDirResourcesNameWithoutDirRecursive(requestedPath, id)
                    .forEach(resourceName -> processResource(zipOut, resourceName, path.resourceName()));
        } catch (Exception e) {
            throw new InternalErrorException("Error during directory streaming for path " + requestedPath, e);
        }
    }

    private void processResource(ZipOutputStream zipOut, String resourceName, String baseFolder) {
        String entryName = getRelativePath(resourceName, baseFolder);
        try {
            zipOut.putNextEntry(new ZipEntry(entryName));
            copyToZip(zipOut, resourceName);
            zipOut.closeEntry();
            zipOut.flush();
        } catch (IOException e) {
            throw new InternalErrorException("Error processing: " + resourceName, e);
        }
    }

    private void copyToZip(ZipOutputStream zipOut, String resourcePath) throws IOException {
        try (InputStream fileStream = downloadStorageService.downloadFile(resourcePath)) {
            fileStream.transferTo(zipOut);
        }
    }

    private String getRelativePath(String fullPath, String baseFolder) {
        String folderWithSlash = baseFolder + SLASH;
        return fullPath.substring(fullPath.indexOf(folderWithSlash) + folderWithSlash.length());
    }

    private static boolean isDirectory(ProcessedPath path) {
        return path.type().equals(Type.DIRECTORY);
    }

}
