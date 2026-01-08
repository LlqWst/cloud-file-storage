package dev.lqwd.cloudfilestorage.service.storage.provider.impl;

import dev.lqwd.cloudfilestorage.exception.InternalErrorException;
import dev.lqwd.cloudfilestorage.infrastructure.UserDirectoryProvider;
import dev.lqwd.cloudfilestorage.infrastructure.path.processor.ProcessedPath;
import dev.lqwd.cloudfilestorage.infrastructure.storage.ResourceStorage;
import dev.lqwd.cloudfilestorage.service.storage.provider.DownloadStorageService;
import dev.lqwd.cloudfilestorage.service.storage.provider.FindStorageService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static dev.lqwd.cloudfilestorage.util.PathConstant.SLASH;


@Service
@RequiredArgsConstructor
public class DownloadStorageServiceImpl implements DownloadStorageService {

    private final UserDirectoryProvider userDirectoryProvider;
    private final ResourceStorage resourceStorage;
    private final FindStorageService findStorageService;

    @Override
    public StreamingResponseBody getFileBytes(long id, String requestedPath) {
        return outputStream -> {
            try (InputStream fileStream = resourceStorage.downloadByPath(
                    userDirectoryProvider.provide(requestedPath, id))) {
                fileStream.transferTo(outputStream);
            } catch (Exception e) {
                throw new InternalErrorException("Error during file streaming for path: " + requestedPath, e);
            }
        };
    }

    @Override
    public StreamingResponseBody getZipBytes(long id, String requestedPath, ProcessedPath path) {
        return outputStream -> {
            try (ZipOutputStream zipOut = new ZipOutputStream(outputStream)) {
                findStorageService.findDirResourcesNameWithoutDirRecursive(requestedPath, id)
                        .forEach(resourceName -> processResource(zipOut, resourceName, path.resourceName()));
            } catch (Exception e) {
                throw new InternalErrorException("Error during directory streaming for path " + requestedPath, e);
            }
        };
    }

    @SneakyThrows
    private void processResource(ZipOutputStream zipOut, String resourceName, String baseFolder) {
        String entryName = getPathAfterBaseFolder(resourceName, baseFolder);
        zipOut.putNextEntry(new ZipEntry(entryName));
        copyToZip(zipOut, resourceName);
        zipOut.closeEntry();
        zipOut.flush();
    }

    @SneakyThrows
    private void copyToZip(ZipOutputStream zipOut, String resourcePath) {
        try (InputStream fileStream = resourceStorage.downloadByPath(resourcePath)) {
            fileStream.transferTo(zipOut);
        }
    }

    private String getPathAfterBaseFolder(String fullPath, String baseFolder) {
        String folderWithSlash = baseFolder + SLASH;
        return fullPath.substring(fullPath.indexOf(folderWithSlash) + folderWithSlash.length());
    }

}