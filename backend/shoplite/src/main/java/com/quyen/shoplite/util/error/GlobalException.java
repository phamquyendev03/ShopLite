package com.quyen.shoplite.util.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalException {

    private Map<String, Object> buildError(int statusCode, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("statusCode", statusCode);
        body.put("message", message);
        body.put("data", null);
        return body;
    }

    @ExceptionHandler(IdInvalidException.class)
    public ResponseEntity<Map<String, Object>> handleIdInvalidException(IdInvalidException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(buildError(400, e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(buildError(400, message));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildError(500, "Lỗi hệ thống: " + e.getMessage()));
    }
}
