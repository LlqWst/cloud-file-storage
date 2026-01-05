package dev.lqwd.cloudfilestorage.infrastructure.parser.storage;

public interface StorageResponseParser<T> {

    ParsedResource pars(T item);

}
