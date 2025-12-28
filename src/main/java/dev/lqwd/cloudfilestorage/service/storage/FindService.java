package dev.lqwd.cloudfilestorage.service.storage;

import dev.lqwd.cloudfilestorage.dto.resource.ResourceResponseDto;
import dev.lqwd.cloudfilestorage.service.proxy.FindProxyService;
import dev.lqwd.cloudfilestorage.infrastructure.PathValidator;
import dev.lqwd.cloudfilestorage.parser.minio.ItemParser;
import dev.lqwd.cloudfilestorage.parser.minio.StatObjectParser;
import dev.lqwd.cloudfilestorage.path_processor.PathProcessor;
import dev.lqwd.cloudfilestorage.path_processor.ProcessedPath;
import dev.lqwd.cloudfilestorage.service.proxy.StorageValidationProxyService;
import io.minio.StatObjectResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@RequiredArgsConstructor
public class FindService {

    private final StatObjectParser statObjectParser;
    private final ItemParser itemParser;
    private final FindProxyService findProxyService;
    private final StorageValidationProxyService validationProxyService;
    private final PathProcessor pathProcessor;
    private final PathValidator validator;


    public ResourceResponseDto getResource(String rawPath, long id) {
        ProcessedPath path = pathProcessor.processResource(rawPath);
        StatObjectResponse statObject = findProxyService.findResource(getRequestedPath(path), id);
        return statObjectParser.pars(statObject);
    }

    public List<ResourceResponseDto> getResources(String rawPath, long id) {
        ProcessedPath path = pathProcessor.processDir(rawPath);
        String requestedPath = getRequestedPath(path);
        validationProxyService.validateOnAbsence(requestedPath, id);

        return findProxyService.findDirResourcesWithoutDir(requestedPath, id)
                .stream()
                .map(itemParser::pars)
                .toList();
    }

    public List<ResourceResponseDto> searchResource(String query, long id) {
        validator.validatePath(query);

        return findProxyService.findAllResources(id)
                .stream()
                .map(itemParser::pars)
                .filter(resource -> resource
                        .name()
                        .toLowerCase()
                        .contains(query.toLowerCase()))
                .toList();
    }

    private static String getRequestedPath(ProcessedPath path) {
        return path.requestedPath();
    }

}
