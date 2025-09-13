package com.imagine.card.card_application.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.imagine.card.card_application.domain.model.ApplicationStatus;
import com.imagine.card.card_application.domain.model.CardApplication;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/* 이벤트 DTO > 상태 전환 (승인/거절 등) : card.status.changed */
/* 관련테이블 : card_application, card_status_history */

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardApplicationStatusChangedEvent {

    private Long appId;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private CardApplication.ApplicationStatus status;

    private String reason;

    private String email;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSS")
    private LocalDateTime changedAt;


    public CardApplicationStatusChangedEvent(CardApplication ca) {
        this.appId = ca.getId();
        this.status = ca.getStatus();
        this.reason = ca.getRejectionReason();
        this.changedAt = LocalDateTime.now();
        this.email = ca.getUser().getEmail();
    }

    @Override
    public String toString() {
        return "CardApplicationStatusChangedEvent{" +
                "appId=" + appId +
                ", status=" + status +
                ", reason='" + reason + '\'' +
                ", email='" + email + '\'' +
                ", changedAt=" + changedAt +
                '}';
    }
}
