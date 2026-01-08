package dev.lqwd.cloudfilestorage.infrastructure;

import dev.lqwd.cloudfilestorage.entity.Type;
import dev.lqwd.cloudfilestorage.exception.BadRequestException;
import dev.lqwd.cloudfilestorage.infrastructure.path.processor.ProcessedPath;
import dev.lqwd.cloudfilestorage.service.storage.provider.ValidationStorageService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import static dev.lqwd.cloudfilestorage.util.PathConstant.SLASH;
import static dev.lqwd.cloudfilestorage.util.RepeatableErrorMessage.MOVE_TO_ITSELF_ERROR_MESSAGE;


@Component
@AllArgsConstructor
public class MoveResourceValidator {

    private final ValidationStorageService validationStorageService;

    public void validate(ProcessedPath pathFrom, ProcessedPath pathTo, long id) {
        String requestedPathFrom = pathFrom.requestedPath();
        String requestedPathTo = pathTo.requestedPath();
        String toParentPath = pathTo.parentPath();

        validateOnEqualsType(pathFrom.type(), pathTo.type());
        validateOnMoveToItself(requestedPathFrom, toParentPath);
        validateOnRootPath(requestedPathFrom);
        validationStorageService.validateParentPath(id, toParentPath);
        validationStorageService.validateOnAbsence(requestedPathFrom, id);
        validationStorageService.validateOnExistence(requestedPathTo, id);
    }

    private static void validateOnMoveToItself(String requestedPathFrom, String toParentPath) {
        if(requestedPathFrom.equals(toParentPath)){
            throw new BadRequestException(MOVE_TO_ITSELF_ERROR_MESSAGE);
        }
    }

    private static void validateOnRootPath(String requestedPathFrom) {
        if (requestedPathFrom.equals(SLASH) || requestedPathFrom.isBlank()) {
            throw new BadRequestException("You can't move the root directory");
        }
    }

    private static void validateOnEqualsType(Type typeFrom, Type typeTo) {
        if (!typeFrom.equals(typeTo)) {
            throw new BadRequestException("The resource types must match");
        }
    }

}
