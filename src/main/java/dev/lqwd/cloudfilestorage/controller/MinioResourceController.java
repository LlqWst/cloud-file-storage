package dev.lqwd.cloudfilestorage.controller;


import dev.lqwd.cloudfilestorage.dto.resource.DownloadedResponseDto;
import dev.lqwd.cloudfilestorage.dto.resource.ResourceResponseDto;
import dev.lqwd.cloudfilestorage.security.user_details.CustomUserDetails;
import dev.lqwd.cloudfilestorage.service.storage.operations.DownloadService;
import dev.lqwd.cloudfilestorage.service.storage.operations.FindService;
import dev.lqwd.cloudfilestorage.service.storage.operations.UploadService;
import dev.lqwd.cloudfilestorage.service.storage.operations.ModificationService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.List;


@RestController
@RequestMapping("/api/resource")
@AllArgsConstructor
public class MinioResourceController extends BaseController {

    private final UploadService uploadService;
    private final FindService findService;
    private final DownloadService downloadService;
    private final ModificationService modificationService;

    @GetMapping
    public ResponseEntity<ResourceResponseDto> getResource(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                           @RequestParam(name = "path") String rawPath) {

        return buildOkResponse(findService.getResource(rawPath, userDetails.getId()));
    }

    @PostMapping
    public ResponseEntity<List<ResourceResponseDto>> uploadResource(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                    @RequestParam("object") MultipartFile[] resources,
                                                                    @RequestParam("path") String rawPath) {

        List<ResourceResponseDto> savedResources = uploadService.upload(rawPath, userDetails.getId(), resources);
        return buildCreatedResponse(savedResources, rawPath);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteResource(@AuthenticationPrincipal CustomUserDetails userDetails,
                                               @RequestParam(name = "path") String rawPath) {

        modificationService.removeResource(rawPath, userDetails.getId());
        return buildNoContentResponse();
    }

    @GetMapping("/move")
    public ResponseEntity<ResourceResponseDto> moveResource(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                            @RequestParam(name = "from") String from,
                                                            @RequestParam(name = "to") String to) {

        return buildOkResponse(modificationService.moveResource(from, to, userDetails.getId()));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ResourceResponseDto>> searchResource(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                    @RequestParam(name = "query") String query) {

        return buildOkResponse(findService.searchResource(query, userDetails.getId()));
    }

    @GetMapping("/download")
    public ResponseEntity<StreamingResponseBody> downloadResource(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                  @RequestParam(name = "path") String rawPath) {

        DownloadedResponseDto download = downloadService.download(rawPath, userDetails.getId());
        return buildOkDownloadResponse(download.content(), download.name());
    }

}