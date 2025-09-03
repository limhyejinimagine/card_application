package com.imagine.card.card_application.event;

/* 이벤트 DTO > 유효성 실패 : card.application.failed */

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ValidationFailedEvent {
    private Long userId;
    private Long cardTypeId;
    private String reason;
}
