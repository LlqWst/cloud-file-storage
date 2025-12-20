package dev.lqwd.cloudfilestorage.controller;


import dev.lqwd.cloudfilestorage.dto.resource.DirectoryResponseDto;
import dev.lqwd.cloudfilestorage.dto.resource.ResourceResponseDto;
import dev.lqwd.cloudfilestorage.security.CustomUserDetails;
import dev.lqwd.cloudfilestorage.service.MinioService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/directory")
@AllArgsConstructor
public class MinioDirectoryController extends BaseController {

    private final MinioService minioService;

    @PostMapping
    public ResponseEntity<ResourceResponseDto> createDir(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                         @RequestParam(name = "path") String rawPath) {

        DirectoryResponseDto directoryResponseDto = minioService.createDir(rawPath, userDetails.getId());
        return buildCreatedResponse(directoryResponseDto, rawPath);
    }

    @GetMapping
    public ResponseEntity<List<ResourceResponseDto>> getResources(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                  @RequestParam(name = "path") String rawPath) {

        return buildOkResponse(minioService.getResources(rawPath, userDetails.getId()));
    }

}