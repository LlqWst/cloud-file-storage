package dev.lqwd.cloudfilestorage.controller.api.annotation;

import dev.lqwd.cloudfilestorage.dto.resource.DirectoryResponseDto;
import dev.lqwd.cloudfilestorage.dto.resource.FileResponseDto;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
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
                                        name = "file",
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
                                        name = "folder",
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
        @ApiResponse(responseCode = "400", description = "invalid path", content = @Content()),
        @ApiResponse(responseCode = "401", description = "User unauthorized", content = @Content()),
        @ApiResponse(responseCode = "404", description = "Resource doesn't exists", content = @Content()),
        @ApiResponse(responseCode = "500", description = "Internal error", content = @Content())
})
public @interface GetResourceResponses {
}
