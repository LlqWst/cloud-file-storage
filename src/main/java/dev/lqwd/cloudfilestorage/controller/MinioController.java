package dev.lqwd.cloudfilestorage.controller;


import dev.lqwd.cloudfilestorage.dto.resource.DirectoryResourceDTO;
import dev.lqwd.cloudfilestorage.dto.resource.ResourceResponseDTO;
import dev.lqwd.cloudfilestorage.entity.Type;
import dev.lqwd.cloudfilestorage.mapper.ResourceResponseMapper;
import dev.lqwd.cloudfilestorage.security.CustomUserDetails;
import dev.lqwd.cloudfilestorage.service.MinioService;
import dev.lqwd.cloudfilestorage.utils.PathValidator;
import dev.lqwd.cloudfilestorage.utils.path_processor.PathProcessor;
import dev.lqwd.cloudfilestorage.utils.path_processor.ProcessedPath;
import lombok.AllArgsConstructor;
import org.springframework.boot.actuate.autoconfigure.observation.ObservationProperties;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.InputStream;
import java.net.URI;
import java.util.List;


@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class MinioController {

    private final MinioService minioService;
    private final PathProcessor pathProcessor;
    private final ResourceResponseMapper mapper;
    private final PathValidator validator;

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
                .body(mapper.toFileResponseDTO(path, 0));
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

    @GetMapping("/resource/search")
    public ResponseEntity<List<ResourceResponseDTO>> search(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                            @RequestParam(name = "query") String query) {

        validator.validatePath(query);
        List<ResourceResponseDTO> resources = minioService.searchResource(query, userDetails.getId());

        return ResponseEntity
                .ok()
                .body(resources);
    }

    @GetMapping("/resource/download")
    public ResponseEntity<StreamingResponseBody> download(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                          @RequestParam(name = "path") String rawPath) {

        ProcessedPath path = pathProcessor.processResource(rawPath);
        StreamingResponseBody content = minioService.download(path, userDetails.getId());

        String name;
        if (path.type().equals(Type.DIRECTORY)) {
            name = path.resourceName() + ".zip";
        } else
            name = path.resourceName();

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + name + "\"")
                .body(content);
    }

    @PostMapping("/resource")
    public ResponseEntity<List<ResourceResponseDTO>> uploadResource(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                          @RequestParam("object") MultipartFile[] files,
                                                          @RequestParam("path") String rawPath) {

        ProcessedPath path = pathProcessor.processDir(rawPath);
        List<ResourceResponseDTO> savedResources = minioService.upload(path, userDetails.getId(), files);


        return ResponseEntity
                .status(HttpStatus.CREATED.ordinal())
                .body(savedResources);
    }

}