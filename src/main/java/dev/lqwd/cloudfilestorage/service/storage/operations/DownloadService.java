package dev.lqwd.cloudfilestorage.service.storage.operations;

import dev.lqwd.cloudfilestorage.dto.resource.DownloadedResponseDto;
import dev.lqwd.cloudfilestorage.exception.InternalErrorException;
import dev.lqwd.cloudfilestorage.service.storage.provider.DownloadStorageService;
import dev.lqwd.cloudfilestorage.service.storage.provider.FindStorageService;
import dev.lqwd.cloudfilestorage.service.storage.provider.ValidationStorageService;
import dev.lqwd.cloudfilestorage.infrastructure.path.processor.PathProcessor;
import dev.lqwd.cloudfilestorage.infrastructure.path.processor.ProcessedPath;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static dev.lqwd.cloudfilestorage.util.PathConstant.SLASH;
import static dev.lqwd.cloudfilestorage.util.PathTypeUtils.isDirectory;


@Service
@RequiredArgsConstructor
public class DownloadService {

    private static final String ZIP_EXTENSION = ".zip";

    private final PathProcessor pathProcessor;
    private final DownloadStorageService downloadStorageService;
    private final ValidationStorageService validationStorageService;
    private final FindStorageService findStorageService;

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
        return outputStream -> {
            try (ZipOutputStream zipOut = new ZipOutputStream(outputStream)) {
                findStorageService.findDirResourcesNameWithoutDirRecursive(requestedPath, id)
                        .forEach(resourceName -> processResource(zipOut, resourceName, path.resourceName()));
            } catch (Exception e) {
                throw new InternalErrorException("Error during directory streaming for path " + requestedPath, e);
            }
        };
    }

    private void processResource(ZipOutputStream zipOut, String resourceName, String baseFolder) {
        String entryName = getPathAfterBaseFolder(resourceName, baseFolder);
        try {
            zipOut.putNextEntry(new ZipEntry(entryName));
            copyToZip(zipOut, resourceName);
            zipOut.closeEntry();
            zipOut.flush();
        } catch (IOException e) {
            throw new InternalErrorException("Error processing resource: " + resourceName, e);
        }
    }

    private void copyToZip(ZipOutputStream zipOut, String resourcePath) throws IOException {
        try (InputStream fileStream = downloadStorageService.downloadFile(resourcePath)) {
            fileStream.transferTo(zipOut);
        }
    }

    private String getPathAfterBaseFolder(String fullPath, String baseFolder) {
        String folderWithSlash = baseFolder + SLASH;
        return fullPath.substring(fullPath.indexOf(folderWithSlash) + folderWithSlash.length());
    }

}
