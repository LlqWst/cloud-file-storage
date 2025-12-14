package dev.lqwd.cloudfilestorage.dto.resource;

import lombok.Builder;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@Builder
public record DownloadedResponseDto(
        StreamingResponseBody content,
        String name
) {}
