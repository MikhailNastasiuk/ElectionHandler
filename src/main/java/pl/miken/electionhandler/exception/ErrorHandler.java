package pl.miken.electionhandler.exception;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(CodedException.class)
    public ErrorData handleRestException(HttpServletResponse response, CodedException exception) {
        exception.logMessage();
        var errorCode = exception.getErrorCode();
        response.setStatus(errorCode.getHttpStatus().value());
        return new ErrorData(errorCode.getCode(), exception.getMessage(), errorCode.getHttpStatus().getReasonPhrase());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorData handleValidationException(
            MethodArgumentNotValidException ex) {

        List<String> errors = new ArrayList<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        errors.add(error.getDefaultMessage())
                );

        return new ErrorData("400", errors.toString(), HttpStatus.BAD_REQUEST.getReasonPhrase());
    }
}
