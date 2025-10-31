package dev.lqwd.cloudfilestorage.controller;


import dev.lqwd.cloudfilestorage.dto.ResourceResponseDTO;
import dev.lqwd.cloudfilestorage.mapper.ResourceResponseMapping;
import dev.lqwd.cloudfilestorage.security.CustomUserDetails;
import dev.lqwd.cloudfilestorage.service.MinioService;
import dev.lqwd.cloudfilestorage.utils.PathProcessor;
import dev.lqwd.cloudfilestorage.model.ProcessedPath;
import dev.lqwd.cloudfilestorage.utils.PathValidator;
import io.minio.messages.Item;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class MinioController {

    MinioService minioService;
    PathValidator validator;
    PathProcessor pathProcessor;
    ResourceResponseMapping mapper;

    @PostMapping("/directory")
    public ResponseEntity<ResourceResponseDTO> createDir(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                          @RequestParam(name = "path") String rawPath) {

        validator.validateDirPath(rawPath);
        ProcessedPath path = pathProcessor.process(rawPath);
        minioService.createNewDir(path, userDetails.getId());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(mapper.toResourceResponseDTO(path));
    }

    @PostMapping("/file")
    public ResponseEntity<ResourceResponseDTO> createFile(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                           @RequestParam(name = "path") String rawPath) {

        validator.validateFilePath(rawPath);
        ProcessedPath path = pathProcessor.process(rawPath);
        minioService.createFile(path, userDetails.getId());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(mapper.toResourceResponseDTO(path));
    }

    @GetMapping("/resource")
    public ResponseEntity<ResourceResponseDTO> getResource(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                           @RequestParam(name = "path") String rawPath) {

        validator.validatePath(rawPath);
        ProcessedPath path = pathProcessor.process(rawPath);
        Item resource = minioService.getResource(path, userDetails.getId());

        return ResponseEntity
                .ok()
                .body(mapper.toResourceResponseDTO(path, resource));
    }


}


