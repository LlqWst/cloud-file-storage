package dev.lqwd.cloudfilestorage.service.storage.provider;

import dev.lqwd.cloudfilestorage.infrastructure.path.processor.ProcessedPath;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

public interface DownloadStorageService {

    StreamingResponseBody getFileBytes(long id, String requestedPath);

    StreamingResponseBody getZipBytes(long id, String requestedPath, ProcessedPath path);
}
