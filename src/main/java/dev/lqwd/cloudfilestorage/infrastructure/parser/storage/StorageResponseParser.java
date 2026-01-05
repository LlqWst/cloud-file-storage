package dev.lqwd.cloudfilestorage.infrastructure.parser.storage;

import dev.lqwd.cloudfilestorage.dto.resource.ParsedResourceDto;

public interface StorageResponseParser<T> {

    ParsedResourceDto pars(T item);

}
