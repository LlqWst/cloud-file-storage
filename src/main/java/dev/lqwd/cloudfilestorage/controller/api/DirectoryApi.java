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
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/directory")
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
            @ApiResponse(
                    responseCode = "201",
                    description = "Directory created",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DirectoryResponseDto.class)
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
                    description = "Parent path doesn't exist",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class),
                            examples = @ExampleObject(
                                    name = "Parent path doesn't exist",
                                    summary = "Parent path doesn't exist",
                                    value = """
                                            {
                                              "message": "Parent path doesn't exist: ${a non-existing parent path}"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Directory already exists",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class),
                            examples = @ExampleObject(
                                    name = "Already exist",
                                    summary = "Directory already exists",
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
    ResponseEntity<DirectoryResponseDto> createDir(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                  @Parameter(
                                                          description = "The path to new folder",
                                                          required = true,
                                                          examples = {
                                                                  @ExampleObject(
                                                                          name = "test folder in root directory",
                                                                          value = "test123/"
                                                                  ),
                                                          }
                                                  )
                                                  @RequestParam(name = "path") String rawPath);

    @GetMapping
    @Operation(
            summary = "Get directory resources",
            description = "Get all resources from a directory"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "A collection of resources stored in a folder has been obtained (not recursively)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    type = "array",
                                    discriminatorProperty = "type",
                                    discriminatorMapping = {
                                            @DiscriminatorMapping(value = "FILE", schema = FileResponseDto.class),
                                            @DiscriminatorMapping(value = "DIRECTORY", schema = DirectoryResponseDto.class)
                                    },
                                    oneOf = {FileResponseDto.class, DirectoryResponseDto.class}
                            ),
                            examples = @ExampleObject(
                                    name = "Example: Resources from root dir",
                                    summary = "resources from root dir",
                                    value = """
                                            [
                                                  {
                                                      "path": "",
                                                      "name": "file",
                                                      "size": 180771,
                                                      "type": "FILE"
                                                  },
                                                  {
                                                      "path": "",
                                                      "name": "folder",
                                                      "type": "DIRECTORY"
                                                  }
                                            ]
                                            """
                            )
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
                    description = "Path doesn't exist",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class),
                            examples = @ExampleObject(
                                    name = "Path doesn't exist",
                                    summary = "Path doesn't exist",
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
    ResponseEntity<List<ResourceResponseDto>> getResources(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                           @Parameter(
                                                                   description = "The path to folder",
                                                                   required = true,
                                                                   examples = {
                                                                           @ExampleObject(
                                                                                   name = "test folder in root directory",
                                                                                   value = "test123/"
                                                                           ),
                                                                   }
                                                           )
                                                           @RequestParam(name = "path") String rawPath);
}