package dev.lqwd.cloudfilestorage.controller;


import dev.lqwd.cloudfilestorage.dto.resource.DownloadedResponseDto;
import dev.lqwd.cloudfilestorage.dto.resource.ResourceResponseDto;
import dev.lqwd.cloudfilestorage.security.CustomUserDetails;
import dev.lqwd.cloudfilestorage.service.MinioService;
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

    private final MinioService minioService;

    @GetMapping
    public ResponseEntity<ResourceResponseDto> getResource(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                           @RequestParam(name = "path") String rawPath) {

        ResourceResponseDto resource = minioService.getResource(rawPath, userDetails.getId());
        return buildOkResponse(resource);
    }

    @PostMapping
    public ResponseEntity<List<ResourceResponseDto>> uploadResource(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                    @RequestParam("object") MultipartFile[] resources,
                                                                    @RequestParam("path") String rawPath) {

        List<ResourceResponseDto> savedResources = minioService.upload(rawPath, userDetails.getId(), resources);
        return buildCreatedResponse(savedResources, rawPath);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteResource(@AuthenticationPrincipal CustomUserDetails userDetails,
                                               @RequestParam(name = "path") String rawPath) {

        minioService.removeResource(rawPath, userDetails.getId());
        return buildNoContentResponse();
    }

    @GetMapping("/move")
    public ResponseEntity<ResourceResponseDto> moveResource(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                            @RequestParam(name = "from") String from,
                                                            @RequestParam(name = "to") String to) {

        ResourceResponseDto resourceResponseDTO = minioService.moveResource(from, to, userDetails.getId());
        return buildOkResponse(resourceResponseDTO);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ResourceResponseDto>> searchResource(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                    @RequestParam(name = "query") String query) {

        List<ResourceResponseDto> resources = minioService.searchResource(query, userDetails.getId());
        return buildOkResponse(resources);
    }

    @GetMapping("/download")
    public ResponseEntity<StreamingResponseBody> downloadResource(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                  @RequestParam(name = "path") String rawPath) {

        DownloadedResponseDto download = minioService.download(rawPath, userDetails.getId());
        return buildDownloadResponse(download.content(), download.name());
    }

}