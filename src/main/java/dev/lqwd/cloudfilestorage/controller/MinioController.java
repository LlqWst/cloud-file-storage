package dev.lqwd.cloudfilestorage.controller;


import dev.lqwd.cloudfilestorage.dto.DirectoryCreatedResponseDTO;
import dev.lqwd.cloudfilestorage.model.Type;
import dev.lqwd.cloudfilestorage.model.CustomUserDetails;
import dev.lqwd.cloudfilestorage.service.MinioService;
import dev.lqwd.cloudfilestorage.utils.PathProcessor;
import dev.lqwd.cloudfilestorage.model.ProcessedPath;
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
    PathProcessor pathProcessor;

    @PostMapping("/directory")
    public ResponseEntity<DirectoryCreatedResponseDTO> getUser(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                               @RequestParam(name = "path") String rawPath) {

        ProcessedPath path = pathProcessor.process(rawPath);
        minioService.createNewDirectory(path.getFullPath(), userDetails.getId());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new DirectoryCreatedResponseDTO(
                        path.getParent(),
                        path.getName(),
                        Type.DIRECTORY
                ));
    }
}
