package ro.axon.dot.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ro.axon.dot.exception.BusinessErrorCode;
import ro.axon.dot.exception.BusinessException;
import ro.axon.dot.exception.ErrorDetails;

import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorDetails> handleBusinessException(BusinessException exception) {
        BusinessErrorCode error = exception.getError();
        HttpStatus status = error.getStatus();

        ErrorDetails errorDetails = ErrorDetails.builder()
                .message(exception.getError().getDevMsg())
                .errorCode(error.getErrorCode())
                .contextVariables(exception.getContextVariables())
                .build();

        return ResponseEntity.status(status)
                .body(errorDetails);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDetails> handleException(MethodArgumentNotValidException exception) {
        ErrorDetails errorDetails = ErrorDetails.builder()
                .message(exception.getBindingResult().getAllErrors().stream()
                        .map(objectError -> Objects.requireNonNull(objectError.getCodes())[0])
                        .collect(Collectors.joining(",")))
                .errorCode(HttpStatus.BAD_REQUEST.toString())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetails);

    }

}
