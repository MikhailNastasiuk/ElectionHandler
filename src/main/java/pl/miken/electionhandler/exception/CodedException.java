package pl.miken.electionhandler.exception;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import pl.miken.electionhandler.enumeration.ErrorCodes;

@Getter
@Setter
@Slf4j
public class CodedException extends RuntimeException{

    private final ErrorCodes errorCode;

    private final String errorMessage;

    public CodedException(ErrorCodes errorCode, String errorMessage) {
        super(errorMessage);
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
    }

    public void logMessage() {
        log.error(this.errorMessage);
    }
}
