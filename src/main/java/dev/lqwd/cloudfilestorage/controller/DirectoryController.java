package dev.lqwd.cloudfilestorage.controller;

import dev.lqwd.cloudfilestorage.controller.api.DirectoryApi;
import dev.lqwd.cloudfilestorage.dto.resource.DirectoryResponseDto;
import dev.lqwd.cloudfilestorage.dto.resource.ResourceResponseDto;
import dev.lqwd.cloudfilestorage.security.user_details.CustomUserDetails;
import dev.lqwd.cloudfilestorage.service.storage.operations.CreationService;
import dev.lqwd.cloudfilestorage.service.storage.operations.FindService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@AllArgsConstructor
public class DirectoryController extends BaseController implements DirectoryApi {

    private final FindService findService;
    private final CreationService creationService;

    @Override
    public ResponseEntity<DirectoryResponseDto> createDir(CustomUserDetails userDetails, String rawPath) {

        DirectoryResponseDto directoryResponseDto = creationService.createDir(rawPath, userDetails.getId());
        return buildCreatedResponse(directoryResponseDto, rawPath);
    }

    @Override
    public ResponseEntity<List<ResourceResponseDto>> getResources(CustomUserDetails userDetails, String rawPath) {

        return buildOkResponse(findService.getResources(rawPath, userDetails.getId()));
    }

}