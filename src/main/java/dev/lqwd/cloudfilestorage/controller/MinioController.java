package dev.lqwd.cloudfilestorage.controller;


import dev.lqwd.cloudfilestorage.dto.resource.ResourceResponseDTO;
import dev.lqwd.cloudfilestorage.mapper.ResourceResponseMapper;
import dev.lqwd.cloudfilestorage.security.CustomUserDetails;
import dev.lqwd.cloudfilestorage.service.minio.MinioService;
import dev.lqwd.cloudfilestorage.utils.path_processor.PathProcessor;
import dev.lqwd.cloudfilestorage.utils.path_processor.ProcessedPath;
import dev.lqwd.cloudfilestorage.utils.PathValidator;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;


@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class MinioController {

    private final MinioService minioService;
    private final PathValidator validator;
    private final PathProcessor pathProcessor;
    private final ResourceResponseMapper mapper;

    @PostMapping("/directory")
    public ResponseEntity<ResourceResponseDTO> createDir(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                          @RequestParam(name = "path") String rawPath) {

        ProcessedPath path = pathProcessor.processDir(rawPath, userDetails.getId());
        minioService.createNewDir(path);

        return ResponseEntity
                .created(URI.create(rawPath))
                .body(mapper.toDirResponseDTO(path));
    }

    @GetMapping("/directory")
    public ResponseEntity<List<ResourceResponseDTO>> getResources(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                 @RequestParam(name = "path") String rawPath) {

        //validator.validateDirPath(rawPath);
        //List<ResourceResponseDTO> resources = minioService.getResources(rawPath, userDetails.getId());
        ProcessedPath path = pathProcessor.processDir(rawPath, userDetails.getId());
        List<ResourceResponseDTO> resources = minioService.getResources(path);

        return ResponseEntity
                .ok()
                .body(resources);
    }

    @PostMapping("/file")
    public ResponseEntity<ResourceResponseDTO> createFile(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                           @RequestParam(name = "path") String rawPath) {

        ProcessedPath path = pathProcessor.processFile(rawPath, userDetails.getId());
        minioService.createFile(path);

        return ResponseEntity
                .created(URI.create(rawPath))
                .body(mapper.toFileResponseDTO(path));
    }

    @GetMapping("/resource")
    public ResponseEntity<ResourceResponseDTO> getResource(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                           @RequestParam(name = "path") String rawPath) {

        ProcessedPath path = pathProcessor.processResource(rawPath, userDetails.getId());
        ResourceResponseDTO resource = minioService.getResource(path);

        return ResponseEntity
                .ok()
                .body(resource);
    }

}