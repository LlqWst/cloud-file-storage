package dev.lqwd.cloudfilestorage.infrastructure.mapper;

import dev.lqwd.cloudfilestorage.dto.resource.DownloadedResponseDto;
import dev.lqwd.cloudfilestorage.infrastructure.path.processor.ProcessedPath;
import org.mapstruct.Mapper;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@Mapper(componentModel = "spring")
public interface DownloadResponseMapper {

    String ZIP_EXTENSION = ".zip";

    default DownloadedResponseDto toFileResponseDto(ProcessedPath path, StreamingResponseBody content){
        return DownloadedResponseDto.builder()
                .content(content)
                .name(path.resourceName())
                .build();
    }

    default DownloadedResponseDto toDirResponseDto(ProcessedPath path, StreamingResponseBody content){
        return DownloadedResponseDto.builder()
                .content(content)
                .name(path.resourceName() + ZIP_EXTENSION)
                .build();
    }

}

