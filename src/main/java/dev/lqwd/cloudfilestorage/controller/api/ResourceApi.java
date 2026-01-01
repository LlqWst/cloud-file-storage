package dev.lqwd.cloudfilestorage.controller.api;

import dev.lqwd.cloudfilestorage.dto.ErrorResponseDto;
import dev.lqwd.cloudfilestorage.dto.resource.DirectoryResponseDto;
import dev.lqwd.cloudfilestorage.dto.resource.FileResponseDto;
import dev.lqwd.cloudfilestorage.dto.resource.ResourceResponseDto;
import dev.lqwd.cloudfilestorage.security.user_details.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.*;
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

@RequestMapping("/api/resource")
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
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Return resource data in json type",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    discriminatorProperty = "type",
                                    discriminatorMapping = {
                                            @DiscriminatorMapping(value = "FILE", schema = FileResponseDto.class),
                                            @DiscriminatorMapping(value = "DIRECTORY", schema = DirectoryResponseDto.class)
                                    },
                                    oneOf = {FileResponseDto.class, DirectoryResponseDto.class}
                            ),
                            examples = {
                                    @ExampleObject(
                                            name = "File",
                                            summary = "File data by path",
                                            value = """
                                                    {
                                                      "path": "",
                                                      "name": "file",
                                                      "size": 180771,
                                                      "type": "FILE"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Folder",
                                            summary = "Folder data by path",
                                            value = """
                                                    {
                                                      "path": "",
                                                      "name": "folder",
                                                      "type": "DIRECTORY"
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "invalid or missing path to the new directory",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class),
                            examples = {
                                    @ExampleObject(
                                            name = "invalid path",
                                            summary = "Path contains incorrect char",
                                            value = """
                                                    {
                                                      "message": "Please enter a resource name that doesn't include any of these characters: [\\\\, ?, >, <, :, *, |]"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Missing path parameter",
                                            summary = "Missing path parameter",
                                            value = """
                                                    {
                                                      "message": "Required request parameter 'path' for method parameter type String is not present"
                                                    }
                                                    """
                                    )
                            }

                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User unauthorized",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class),
                            examples = @ExampleObject(
                                    name = "User unauthorized",
                                    summary = "User unauthorized",
                                    value = """
                                            {
                                              "message": "Unauthorized user"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Resource doesn't exists",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class),
                            examples = @ExampleObject(
                                    name = "Resource doesn't exists",
                                    summary = "Resource doesn't exists",
                                    value = """
                                            {
                                              "message": "Resource doesn't exists: ${folder name}"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    })
    ResponseEntity<ResourceResponseDto> getResource(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                    @Parameter(
                                                            description = "The path to resource",
                                                            required = true,
                                                            examples = {
                                                                    @ExampleObject(
                                                                            name = "test resource in root directory",
                                                                            value = "file_test.txt"
                                                                    ),
                                                            }
                                                    )
                                                    @RequestParam(name = "path") String rawPath);

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Upload resource in storage",
            description = "Upload a resource by the specified path"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "The resource has been successfully uploaded to the storage.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    discriminatorProperty = "type",
                                    discriminatorMapping = {
                                            @DiscriminatorMapping(value = "FILE", schema = FileResponseDto.class),
                                            @DiscriminatorMapping(value = "DIRECTORY", schema = DirectoryResponseDto.class)
                                    },
                                    oneOf = {FileResponseDto.class, DirectoryResponseDto.class}
                            ),
                            examples = {
                                    @ExampleObject(
                                            name = "File",
                                            summary = "File data by path",
                                            value = """
                                                    {
                                                      "path": "",
                                                      "name": "file",
                                                      "size": 180771,
                                                      "type": "FILE"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Folder",
                                            summary = "Folder data by path",
                                            value = """
                                                    {
                                                      "path": "",
                                                      "name": "folder",
                                                      "type": "DIRECTORY"
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request body",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class),
                            examples = {
                                    @ExampleObject(
                                            name = "invalid path",
                                            summary = "Path contains incorrect char",
                                            value = """
                                                    {
                                                      "message": "Please enter a resource name that doesn't include any of these characters: [\\\\, ?, >, <, :, *, |]"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Missing path parameter",
                                            summary = "Missing path parameter",
                                            value = """
                                                    {
                                                      "message": "Required request parameter 'path' for method parameter type String is not present"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "incorrect body",
                                            summary = "incorrect body type",
                                            value = """
                                                    {
                                                      "message": "Current request is not a multipart request"
                                                    }
                                                    """
                                    )
                            }

                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User unauthorized",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class),
                            examples = @ExampleObject(
                                    name = "User unauthorized",
                                    summary = "User unauthorized",
                                    value = """
                                            {
                                              "message": "Unauthorized user"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Resource already exists",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class),
                            examples = @ExampleObject(
                                    name = "Already exist",
                                    summary = "Resource already exists",
                                    value = """
                                            {
                                              "message": "Resource already exists: ${folder name}"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    })
    ResponseEntity<List<ResourceResponseDto>> uploadResource(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                             @Parameter(
                                                                     description = "Max 40 files, each up to 20MB, Total upload Max 30MB",
                                                                     required = true
                                                             )
                                                             @RequestPart("object") MultipartFile[] resources,
                                                             @Parameter(
                                                                     description = "The path where the resource should be uploaded",
                                                                     required = true,
                                                                     examples = {
                                                                             @ExampleObject(
                                                                                     name = "Root directory for files",
                                                                                     value = "/"
                                                                             ),
                                                                     }
                                                             )
                                                             @RequestParam("path") String rawPath);

    @DeleteMapping
    @Operation(
            summary = "Deletion of resource from storage",
            description = "Deletion of resource by path"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "The resource has been successfully deleted from the storage.",
                    content = @Content(
                            mediaType = "application/json"
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid or missing path",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class),
                            examples = {
                                    @ExampleObject(
                                            name = "invalid path",
                                            summary = "Path contains incorrect char",
                                            value = """
                                                    {
                                                      "message": "Please enter a resource name that doesn't include any of these characters: [\\\\, ?, >, <, :, *, |]"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Missing path parameter",
                                            summary = "Missing path parameter",
                                            value = """
                                                    {
                                                      "message": "Required request parameter 'path' for method parameter type String is not present"
                                                    }
                                                    """
                                    ),
                            }

                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User unauthorized",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class),
                            examples = @ExampleObject(
                                    name = "User unauthorized",
                                    summary = "User unauthorized",
                                    value = """
                                            {
                                              "message": "Unauthorized user"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Resource path doesn't exist",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class),
                            examples = @ExampleObject(
                                    name = "Resource path doesn't exist",
                                    summary = "Resource path doesn't exist",
                                    value = """
                                            {
                                              "message": "Resource doesn't exist: ${a non-existing path}"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    })
    ResponseEntity<Void> deleteResource(@AuthenticationPrincipal CustomUserDetails userDetails,
                                        @Parameter(
                                                description = "The path to the resource to delete",
                                                required = true,
                                                examples = {
                                                        @ExampleObject(
                                                                name = "Test directory for deletion",
                                                                value = "folder1/"
                                                        ),
                                                }
                                        )
                                        @RequestParam(name = "path") String rawPath);

    @GetMapping("/move")
    @Operation(
            summary = "Moving/renaming a resource",
            description = "Moving a resource to a new directory or rename"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "The resource has been successfully moved/renamed",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = FileResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid or missing path",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class),
                            examples = {
                                    @ExampleObject(
                                            name = "invalid path",
                                            summary = "Path contains incorrect char",
                                            value = """
                                                    {
                                                      "message": "Please enter a resource name that doesn't include any of these characters: [\\\\, ?, >, <, :, *, |]"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Missing path parameter",
                                            summary = "Missing path parameter",
                                            value = """
                                                    {
                                                      "message": "Required request parameter 'path' for method parameter type String is not present"
                                                    }
                                                    """
                                    ),
                            }

                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User unauthorized",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class),
                            examples = @ExampleObject(
                                    name = "User unauthorized",
                                    summary = "User unauthorized",
                                    value = """
                                            {
                                              "message": "Unauthorized user"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Resource for move/rename doesn't exist",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class),
                            examples = @ExampleObject(
                                    name = "Resource doesn't exist",
                                    summary = "Resource doesn't exist",
                                    value = """
                                            {
                                              "message": "Resource doesn't exist: ${a non-existing path}"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "The resource exists in the 'To' path",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class),
                            examples = @ExampleObject(
                                    name = "Already exist",
                                    summary = "Resource already exists",
                                    value = """
                                            {
                                              "message": "Resource already exists: ${folder name}"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    })
    ResponseEntity<ResourceResponseDto> moveResource(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                     @Parameter(
                                                             description = "Location of the resource to be moved",
                                                             required = true,
                                                             examples = {
                                                                     @ExampleObject(
                                                                             name = "Path 'from'",
                                                                             value = "folder1/test.txt"
                                                                     ),
                                                             }
                                                     )
                                                     @RequestParam(name = "from") String from,
                                                     @Parameter(
                                                             description = "New resource location",
                                                             required = true,
                                                             examples = {
                                                                     @ExampleObject(
                                                                             name = "Path 'to'",
                                                                             value = "folder1/folder2/test.txt"
                                                                     ),
                                                             }
                                                     )
                                                     @RequestParam(name = "to") String to);

    @GetMapping("/search")
    @Operation(
            summary = "Search among resources",
            description = "Search based on a query among all user resources"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "The resource has been successfully fined",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = FileResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid or missing query",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class),
                            examples = {
                                    @ExampleObject(
                                            name = "invalid path",
                                            summary = "Path contains incorrect char",
                                            value = """
                                                    {
                                                      "message": "Please enter a resource name that doesn't include any of these characters: [\\\\, ?, >, <, :, *, |]"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Missing path parameter",
                                            summary = "Missing path parameter",
                                            value = """
                                                    {
                                                      "message": "Required request parameter 'path' for method parameter type String is not present"
                                                    }
                                                    """
                                    ),
                            }

                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User unauthorized",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class),
                            examples = @ExampleObject(
                                    name = "User unauthorized",
                                    summary = "User unauthorized",
                                    value = """
                                            {
                                              "message": "Unauthorized user"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    })
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
            @ApiResponse(
                    responseCode = "200",
                    description = "The resource has been successfully downloaded",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = FileResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid or missing path",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class),
                            examples = {
                                    @ExampleObject(
                                            name = "invalid path",
                                            summary = "Path contains incorrect char",
                                            value = """
                                                    {
                                                      "message": "Please enter a resource name that doesn't include any of these characters: [\\\\, ?, >, <, :, *, |]"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Missing path parameter",
                                            summary = "Missing path parameter",
                                            value = """
                                                    {
                                                      "message": "Required request parameter 'path' for method parameter type String is not present"
                                                    }
                                                    """
                                    ),
                            }

                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User unauthorized",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class),
                            examples = @ExampleObject(
                                    name = "User unauthorized",
                                    summary = "User unauthorized",
                                    value = """
                                            {
                                              "message": "Unauthorized user"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Resource for downloading doesn't exist",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class),
                            examples = @ExampleObject(
                                    name = "Resource doesn't exist",
                                    summary = "Resource doesn't exist",
                                    value = """
                                            {
                                              "message": "Resource doesn't exist: ${a non-existing path}"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    })
    ResponseEntity<StreamingResponseBody> downloadResource(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                           @Parameter(
                                                                   description = "Location of the download resource",
                                                                   required = true,
                                                                   examples = {
                                                                           @ExampleObject(
                                                                                   name = "Path to resource",
                                                                                   value = "folder1/"
                                                                           ),
                                                                   }
                                                           )
                                                           @RequestParam(name = "path") String rawPath);

}