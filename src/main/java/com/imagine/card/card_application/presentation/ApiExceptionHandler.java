package com.imagine.card.card_application.presentation;


import com.imagine.card.card_application.application.service.exception.DomainException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;

// 공통 에러 핸들러
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<?> handleDomain(DomainException e) {
        return ResponseEntity.status(409).body(Map.of(
                "timestamp" , Instant.now().toString(),
                "error", "CONFLICT",
                "message", e.getMessage()
        ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneric(Exception e) {
        return ResponseEntity.badRequest().body(Map.of(
                "timestamp" , Instant.now().toString(),
                "error","BAD_REQUEST",
                "message", e.getMessage()
        ));
    }
}
