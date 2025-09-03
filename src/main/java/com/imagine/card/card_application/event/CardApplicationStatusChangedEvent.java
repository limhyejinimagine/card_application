package com.imagine.card.card_application.event;

import com.imagine.card.card_application.domain.model.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/* 이벤트 DTO > 상태 전환 (승인/거절 등) : card.status.changed */

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardApplicationStatusChangedEvent {
    private Long appId;
    private ApplicationStatus status;
}
