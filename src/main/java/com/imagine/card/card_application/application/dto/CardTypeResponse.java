
package com.imagine.card.card_application.application.dto;

import com.imagine.card.card_application.domain.model.CardType;

public record CardTypeResponse(Long  applicationId,
                               String name,
                               String description,
                               boolean isActive

) {
    // DB 조회시 엔티티(CardType) 를 그대로 반환하지 않고, CardTypeResponse DTO 로 변환해서 리턴
    public static CardTypeResponse from(CardType cardType) {
        return new CardTypeResponse(
                cardType.getId(),
                cardType.getName(),
                cardType.getDescription(),
                cardType.getIsActive()
        );
    }
}