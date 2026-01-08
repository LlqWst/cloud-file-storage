package dev.lqwd.cloudfilestorage.infrastructure.storage.parser;

public interface StorageResponseParser<T> {

    ParsedResource parse(T item);

}
