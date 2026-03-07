package com.manas.exception;

import com.manas.config.TraceIdFilter;
import com.manas.model.response.ErrorDetail;
import com.manas.model.response.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.*;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private ErrorResponse createErrorResponse(String code, String message, Map<String,
            List<String>> fields){
        String traceIdKey = MDC.get(TraceIdFilter.TRACE_ID_KEY);
        if(traceIdKey == null){
            traceIdKey = UUID.randomUUID().toString();
        }

        ErrorDetail errorDetail = ErrorDetail.builder()
                .code(code)
                .message(message)
                .fields(fields)
                .traceId(traceIdKey).build();

        return new ErrorResponse(errorDetail);

    }

    private void logError(Exception ex, String traceIdKey){
        logger.error("Exception [traceId={}]:{}", traceIdKey, ex.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(createErrorResponse("resource_not_found", ex.getMessage(), null));
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientBalance  (InsufficientBalanceException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(createErrorResponse("insufficient_balance", ex.getMessage(), null));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, List<String>> fields = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            fields.computeIfAbsent(error.getField(), key -> new ArrayList<>())
                    .add(error.getDefaultMessage());
        });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(createErrorResponse("validation_failed", "Invalid request parameters", fields));
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<ErrorResponse> handleBusinessExceptions(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(createErrorResponse("bad_request", ex.getMessage(), null));
    }


}
