package com.imagine.card.card_application.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class UpdateApplicationStatusResponse {
    private Long applicationId;
    private String status;
    private LocalDateTime changedAt;
    private String message;
}
