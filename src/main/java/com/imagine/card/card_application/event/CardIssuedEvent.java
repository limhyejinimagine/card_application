package com.imagine.card.card_application.event;

import com.imagine.card.card_application.domain.model.CardApplication;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/* 이벤트 DTO > 발급 완료 : card.issued */

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardIssuedEvent {
    private Long appId;
    private Long userId;
    private String cardNumber;
    private CardApplication.ApplicationStatus status;
    private String email;
    private String reason;
    private LocalDateTime issueDate;
}