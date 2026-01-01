package dev.lqwd.cloudfilestorage.controller;

import dev.lqwd.cloudfilestorage.controller.api.ResourceApi;
import dev.lqwd.cloudfilestorage.dto.resource.DownloadedResponseDto;
import dev.lqwd.cloudfilestorage.dto.resource.ResourceResponseDto;
import dev.lqwd.cloudfilestorage.security.user_details.CustomUserDetails;
import dev.lqwd.cloudfilestorage.service.storage.operations.DownloadService;
import dev.lqwd.cloudfilestorage.service.storage.operations.FindService;
import dev.lqwd.cloudfilestorage.service.storage.operations.UploadService;
import dev.lqwd.cloudfilestorage.service.storage.operations.ModificationService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.List;


@RestController
@AllArgsConstructor
public class ResourceController extends BaseController implements ResourceApi {

    private final UploadService uploadService;
    private final FindService findService;
    private final DownloadService downloadService;
    private final ModificationService modificationService;

    @Override
    public ResponseEntity<ResourceResponseDto> getResource(CustomUserDetails userDetails, String rawPath) {

        return buildOkResponse(findService.getResource(rawPath, userDetails.getId()));
    }

    @Override
    public ResponseEntity<List<ResourceResponseDto>> uploadResource(CustomUserDetails userDetails,
                                                                    MultipartFile[] resources,
                                                                    String rawPath) {

        List<ResourceResponseDto> savedResources = uploadService.upload(rawPath, userDetails.getId(), resources);
        return buildCreatedResponse(savedResources, rawPath);
    }

    @Override
    public ResponseEntity<Void> deleteResource(CustomUserDetails userDetails, String rawPath) {

        modificationService.removeResource(rawPath, userDetails.getId());
        return buildNoContentResponse();
    }

    @Override
    public ResponseEntity<ResourceResponseDto> moveResource(CustomUserDetails userDetails, String from, String to) {

        return buildOkResponse(modificationService.moveResource(from, to, userDetails.getId()));
    }

    @Override
    public ResponseEntity<List<ResourceResponseDto>> searchResource(CustomUserDetails userDetails, String query) {

        return buildOkResponse(findService.searchResource(query, userDetails.getId()));
    }

    @Override
    public ResponseEntity<StreamingResponseBody> downloadResource(CustomUserDetails userDetails, String rawPath) {

        DownloadedResponseDto download = downloadService.download(rawPath, userDetails.getId());
        return buildOkDownloadResponse(download.content(), download.name());
    }

}