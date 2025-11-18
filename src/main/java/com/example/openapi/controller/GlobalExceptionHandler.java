package com.example.openapi.controller;

import com.example.openapi.aspect.RateLimitExceededException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<Map<String, Object>> handleRateLimitExceeded(RateLimitExceededException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "Rate Limit Exceeded");
        body.put("message", ex.getMessage());
        body.put("status", HttpStatus.TOO_MANY_REQUESTS.value());
        
        return ResponseEntity
                .status(HttpStatus.TOO_MANY_REQUESTS)
                .body(body);
    }
}

