package com.imagine.card.card_application.event;

import com.imagine.card.card_application.domain.model.CardApplication;
import lombok.*;

/* 이벤트 DTO > 신청 성공 : card.application.requested */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardApplicationRequestedEvent {
    private Long appId;
    private Long userId;
    private Long cardTypeId;

    public CardApplicationRequestedEvent(CardApplication app) {
        this.appId = app.getId();
        this.userId = app.getUser().getId();
        this.cardTypeId = app.getCardType().getId();
    }
}