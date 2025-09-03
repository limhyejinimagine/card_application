package com.imagine.card.card_application.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/* 이벤트 DTO > 신청 성공 : card.application.requested */

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardApplicationRequestedEvent {
    private Long appId;
    private Long userId;
    private Long cardTypeId;
}