package dev.lqwd.cloudfilestorage.controller.api;

import dev.lqwd.cloudfilestorage.controller.api.annotation.DirResourcesResponses;
import dev.lqwd.cloudfilestorage.controller.api.annotation.FolderPathParam;
import dev.lqwd.cloudfilestorage.dto.resource.DirectoryResponseDto;
import dev.lqwd.cloudfilestorage.dto.resource.ResourceResponseDto;
import dev.lqwd.cloudfilestorage.security.userdetails.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequestMapping(value = "/api/directory", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(
        name = "Directory operations controller",
        description = "The controller creates a folder or returns the resources of the directory"
)
public interface DirectoryApi {

    @PostMapping
    @Operation(
            summary = "Create new folder",
            description = "Creation of new folder in cloud storage for authorized user"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Directory created"),
            @ApiResponse(responseCode = "400", description = "invalid path", content = @Content()),
            @ApiResponse(responseCode = "401", description = "User unauthorized", content = @Content()),
            @ApiResponse(responseCode = "404", description = "Parent path doesn't exist", content = @Content()),
            @ApiResponse(responseCode = "409", description = "Directory already exists", content = @Content()),
            @ApiResponse(responseCode = "500", description = "Internal error", content = @Content())
    })
    ResponseEntity<DirectoryResponseDto> createDir(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                   @FolderPathParam
                                                   @RequestParam(name = "path") String rawPath);

    @GetMapping
    @Operation(
            summary = "Get directory resources",
            description = "Get all resources from a directory"
    )
    @DirResourcesResponses
    ResponseEntity<List<ResourceResponseDto>> getResources(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                           @FolderPathParam
                                                           @RequestParam(name = "path") String rawPath);
}