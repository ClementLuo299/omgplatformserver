package omgplatform.server.utils;

import omgplatform.server.utils.LoggingUtil;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Handles exceptions.
 *
 * @authors Clement Luo,
 * @date May 11, 2025
 * @edited June 29, 2025
 * @since 1.0
 */
@RestControllerAdvice
public class ExceptionHandler {

    public ExceptionHandler() {
        LoggingUtil.info("Global Exception Handler initialized");
    }

    /**
     *
     */
    @org.springframework.web.bind.annotation.ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {
        LoggingUtil.methodEntry("handleValidationErrors", Map.of(
            "exceptionType", ex.getClass().getSimpleName(),
            "message", ex.getMessage()
        ));
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(err -> {
            errors.put(err.getField(), err.getDefaultMessage());
            LoggingUtil.warn("Validation error", Map.of(
                "field", err.getField(),
                "error", err.getDefaultMessage(),
                "rejectedValue", err.getRejectedValue() != null ? err.getRejectedValue().toString() : "null"
            ));
        });
        
        LoggingUtil.info("Validation errors handled", Map.of(
            "errorCount", errors.size(),
            "statusCode", HttpStatus.BAD_REQUEST.value()
        ));
        
        LoggingUtil.methodExit("handleValidationErrors", "Returning " + errors.size() + " validation errors");
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    /**
     *
     */
    @org.springframework.web.bind.annotation.ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgs(IllegalArgumentException ex) {
        LoggingUtil.methodEntry("handleIllegalArgs", Map.of(
            "exceptionType", ex.getClass().getSimpleName(),
            "message", ex.getMessage()
        ));
        
        LoggingUtil.warn("Illegal argument exception handled", Map.of(
            "message", ex.getMessage(),
            "statusCode", HttpStatus.BAD_REQUEST.value()
        ));
        
        LoggingUtil.methodExit("handleIllegalArgs", "Returning bad request response");
        return new ResponseEntity<>(Map.of("error", ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    /**
     *
     */
    @org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleAllExceptions(Exception ex) {
        LoggingUtil.methodEntry("handleAllExceptions", Map.of(
            "exceptionType", ex.getClass().getSimpleName(),
            "message", ex.getMessage()
        ));
        
        LoggingUtil.error("Unhandled exception occurred", ex);
        
        LoggingUtil.methodExit("handleAllExceptions", "Returning internal server error response");
        return new ResponseEntity<>(Map.of("error", ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
