package com.imagine.card.card_application.domain.model;

/* Outbox 이벤트 발행 상태 값  */
public enum OutboxStatus {
    NEW,    // 신규
    SENT,   // 이벤트 발행 완료
    FAILED  // 이벤트 발행 실패
}
