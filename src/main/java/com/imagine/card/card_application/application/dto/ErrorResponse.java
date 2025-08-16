package com.imagine.card.card_application.application.dto;

import java.time.LocalDateTime;
import java.util.Map;

public record ErrorResponse(
        String code,
        String message,
        LocalDateTime timestamp,
        Map<String, Object> details
){
    public static ErrorResponse of(String code, String message, LocalDateTime timestamp, Map<String, Object> details){
        return new ErrorResponse(code, message, LocalDateTime.now(), details);
    }
}