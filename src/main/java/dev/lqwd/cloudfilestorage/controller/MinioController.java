package dev.lqwd.cloudfilestorage.controller;


import dev.lqwd.cloudfilestorage.dto.resource.DirectoryResourceDTO;
import dev.lqwd.cloudfilestorage.dto.resource.ResourceResponseDTO;
import dev.lqwd.cloudfilestorage.mapper.ResourceResponseMapper;
import dev.lqwd.cloudfilestorage.security.CustomUserDetails;
import dev.lqwd.cloudfilestorage.service.MinioService;
import dev.lqwd.cloudfilestorage.utils.path_processor.PathProcessor;
import dev.lqwd.cloudfilestorage.utils.path_processor.ProcessedPath;
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
    private final PathProcessor pathProcessor;
    private final ResourceResponseMapper mapper;

    @PostMapping("/directory")
    public ResponseEntity<ResourceResponseDTO> createDir(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                         @RequestParam(name = "path") String rawPath) {

        ProcessedPath path = pathProcessor.processDir(rawPath);
        minioService.createNewDir(path, userDetails.getId());

        return ResponseEntity
                .created(URI.create(rawPath))
                .body(mapper.toDirResponseDTO(path));
    }

    @GetMapping("/directory")
    public ResponseEntity<List<ResourceResponseDTO>> getResources(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                  @RequestParam(name = "path") String rawPath) {

        ProcessedPath path = pathProcessor.processDir(rawPath);
        List<ResourceResponseDTO> resources = minioService.getResources(path, userDetails.getId());

        return ResponseEntity
                .ok()
                .body(resources);
    }

    @PostMapping("/file")
    public ResponseEntity<ResourceResponseDTO> createFile(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                          @RequestParam(name = "path") String rawPath) {

        ProcessedPath path = pathProcessor.processFile(rawPath);
        minioService.createFile(path, userDetails.getId());

        return ResponseEntity
                .created(URI.create(rawPath))
                .body(mapper.toFileResponseDTO(path));
    }

    @GetMapping("/resource")
    public ResponseEntity<ResourceResponseDTO> getResource(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                           @RequestParam(name = "path") String rawPath) {

        ProcessedPath path = pathProcessor.processResource(rawPath);
        ResourceResponseDTO resource = minioService.getResource(path, userDetails.getId());

        return ResponseEntity
                .ok()
                .body(resource);
    }

    @DeleteMapping("/resource")
    public ResponseEntity<Void> deleteResource(@AuthenticationPrincipal CustomUserDetails userDetails,
                                               @RequestParam(name = "path") String rawPath) {

        ProcessedPath path = pathProcessor.processResource(rawPath);
        minioService.removeResource(path, userDetails.getId());

        return ResponseEntity
                .noContent()
                .build();
    }

    @GetMapping("/resource/move")
    public ResponseEntity<DirectoryResourceDTO> moveResource(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                             @RequestParam(name = "from") String from,
                                                             @RequestParam(name = "to") String to) {

        ProcessedPath pathFrom = pathProcessor.processResource(from);
        ProcessedPath pathTo = pathProcessor.processResource(to);
        minioService.moveResource(pathFrom, pathTo, userDetails.getId());

        return ResponseEntity
                .ok()
                .body(mapper.toDirResponseDTO(pathTo));
    }

}