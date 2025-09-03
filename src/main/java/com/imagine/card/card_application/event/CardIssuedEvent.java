package com.imagine.card.card_application.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/* 이벤트 DTO > 발급 완료 : card.issued */

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardIssuedEvent {
    private Long appId;
    private Long userId;
}