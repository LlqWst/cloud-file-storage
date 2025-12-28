package dev.lqwd.cloudfilestorage.service.storage;

import dev.lqwd.cloudfilestorage.dto.resource.DownloadedResponseDto;
import dev.lqwd.cloudfilestorage.entity.Type;
import dev.lqwd.cloudfilestorage.exception.InternalErrorException;
import dev.lqwd.cloudfilestorage.service.proxy.DownloadProxyService;
import dev.lqwd.cloudfilestorage.service.proxy.FindProxyService;
import dev.lqwd.cloudfilestorage.path_processor.PathProcessor;
import dev.lqwd.cloudfilestorage.path_processor.ProcessedPath;
import dev.lqwd.cloudfilestorage.service.proxy.StorageValidationProxyService;
import io.minio.messages.Item;
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
    private final DownloadProxyService downloadProxyService;
    private final StorageValidationProxyService validationProxyService;
    private final FindProxyService findProxyService;


    public DownloadedResponseDto download(String rawPath, long id) {
        ProcessedPath path = pathProcessor.processResource(rawPath);
        String requestedPath = path.requestedPath();
        validationProxyService.validateOnAbsence(requestedPath, id);

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
            try (InputStream fileStream = downloadProxyService.downloadFile(requestedPath, id)) {
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
            findProxyService.findDirResourcesWithoutDirRecursive(requestedPath, id)
                    .forEach(resource -> processResource(zipOut, resource, path.resourceName()));
        } catch (Exception e) {
            throw new InternalErrorException("Error during directory streaming for path " + requestedPath, e);
        }
    }

    private void processResource(ZipOutputStream zipOut, Item resource, String baseFolder) {
        String entryName = getRelativePath(resource.objectName(), baseFolder);

        try {
            zipOut.putNextEntry(new ZipEntry(entryName));
            copyToZip(zipOut, resource.objectName());
            zipOut.closeEntry();
            zipOut.flush();
        } catch (IOException e) {
            throw new InternalErrorException("Error processing: " + resource.objectName(), e);
        }
    }

    private void copyToZip(ZipOutputStream zipOut, String resourcePath) throws IOException {
        try (InputStream fileStream = downloadProxyService.downloadFile(resourcePath)) {
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
