package dev.lqwd.cloudfilestorage.controller.api;

import dev.lqwd.cloudfilestorage.controller.api.annotation.*;
import dev.lqwd.cloudfilestorage.dto.resource.ResourceResponseDto;
import dev.lqwd.cloudfilestorage.security.userdetails.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.List;


@RequestMapping(value = "/api/resource", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(
        name = "Resource operations controller",
        description = "Operations involving changing, uploading, and downloading resources"
)
public interface ResourceApi {

    @GetMapping
    @Operation(
            summary = "Get resource by path",
            description = "Get resource by path"
    )
    @GetResourceResponses
    ResponseEntity<ResourceResponseDto> getResource(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                    @ResourcePathParam
                                                    @RequestParam(name = "path") String rawPath);

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Upload resource in storage",
            description = "Upload a resource by the specified path"
    )
    @UploadResponses
    ResponseEntity<List<ResourceResponseDto>> uploadResource(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                             @Parameter(
                                                                     description = "Max 40 files," +
                                                                                   " each up to 20MB," +
                                                                                   " Total upload Max 30MB",
                                                                     required = true
                                                             )
                                                             @RequestPart("object") MultipartFile[] resources,
                                                             @FolderPathParam
                                                             @RequestParam("path") String rawPath);

    @DeleteMapping
    @Operation(
            summary = "Deletion of resource from storage",
            description = "Deletion of resource by path"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "The resource has been removed"),
            @ApiResponse(responseCode = "400", description = "Invalid or missing path", content = @Content()),
            @ApiResponse(responseCode = "401", description = "User unauthorized", content = @Content()),
            @ApiResponse(responseCode = "404", description = "Resource path doesn't exist", content = @Content()),
            @ApiResponse(responseCode = "500", description = "Internal error", content = @Content())
    })
    ResponseEntity<Void> deleteResource(@AuthenticationPrincipal CustomUserDetails userDetails,
                                        @ResourcePathParam
                                        @RequestParam(name = "path") String rawPath);

    @GetMapping("/move")
    @Operation(
            summary = "Moving/renaming a resource",
            description = "Moving a resource to a new directory or rename"
    )
    @MoveResponses
    ResponseEntity<ResourceResponseDto> moveResource(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                     @FolderPathParam
                                                     @RequestParam(name = "from") String from,
                                                     @FolderPathParam
                                                     @RequestParam(name = "to") String to);

    @GetMapping("/search")
    @Operation(
            summary = "Search among resources",
            description = "Search based on a query among all user resources"
    )
    @SearchResponses
    ResponseEntity<List<ResourceResponseDto>> searchResource(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                             @Parameter(
                                                                     description = "Resource search query",
                                                                     required = true,
                                                                     examples = {
                                                                             @ExampleObject(
                                                                                     name = "Query",
                                                                                     value = "file"
                                                                             ),
                                                                     }
                                                             )
                                                             @RequestParam(name = "query") String query);

    @GetMapping("/download")
    @Operation(
            summary = "Downloading a resource based on a path",
            description = "Downloading a folder in zip. The file in the original extension"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The resource has been successfully downloaded",
                    content = @Content(mediaType = "application/octet-stream")
            ),
            @ApiResponse(responseCode = "400", description = "Invalid path", content = @Content()),
            @ApiResponse(responseCode = "401", description = "User unauthorized", content = @Content()),
            @ApiResponse(responseCode = "404", description = "Resource doesn't exist", content = @Content()),
            @ApiResponse(responseCode = "500", description = "Internal error", content = @Content())
    })
    ResponseEntity<StreamingResponseBody> downloadResource(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                           @FolderPathParam
                                                           @RequestParam(name = "path") String rawPath);

}