package pl.miken.electionhandler.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCodes {

    USER_WITH_ID_NOT_FOUND("100001", "User with ID = [%d] not found", HttpStatus.NOT_FOUND),
    USER_WITH_PERSONAL_NUMBER_ALREADY_EXITS("100002", "User with personal number = [%s] already exists", HttpStatus.BAD_REQUEST),

    VOTE_WITH_IDENTIFIER_NOT_FOUND("200001", "Vote with identifier = [%s] not found", HttpStatus.NOT_FOUND),
    VOTE_WITH_IDENTIFIER_ALREADY_EXITS("200002", "Vote with identifier = [%s] already exists", HttpStatus.BAD_REQUEST),
    CANT_START_NEW_VOTE("200003", "Vote with identifier = [%s] already in process", HttpStatus.BAD_REQUEST),
    CANT_CHANGE_VOTE("200004", "Can't change vote: %s", HttpStatus.BAD_REQUEST),

    ACTIVE_VOTE_WITH_IDENTIFIER_NOT_FOUND("300001", "Active vote with identifier = [%s] not found", HttpStatus.NOT_FOUND),
    ACTIVE_VOTE_WITH_ID_NOT_FOUND("300002", "Active vote with id = [%d] not found", HttpStatus.NOT_FOUND),
    USER_ALREADY_VOTED("300003", "User with id = [%d] already voted", HttpStatus.BAD_REQUEST),

    OPTION_WITH_NAME_ALREADY_EXITS("400001", "Option with name = [%s] already exists", HttpStatus.BAD_REQUEST),
    OPTION_WITH_ID_NOT_FOUND("400002", "Option with id = [%d] not found", HttpStatus.BAD_REQUEST),

    DESERIALIZATION_ERROR("800001", "Deserialization error: %s", HttpStatus.INTERNAL_SERVER_ERROR),
    SERIALIZATION_ERROR("800002", "Serialization error: %s", HttpStatus.INTERNAL_SERVER_ERROR),

    NOT_IMPLEMENTED("999999", "Functionality not implemented", HttpStatus.NOT_IMPLEMENTED);

    private final String code;

    private final String message;

    private final HttpStatus httpStatus;
}
