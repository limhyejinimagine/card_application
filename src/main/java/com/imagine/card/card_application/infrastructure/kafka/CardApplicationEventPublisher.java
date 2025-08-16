package com.imagine.card.card_application.infrastructure.kafka;

// 이벤트 퍼블리셔 인터페이스
public interface CardApplicationEventPublisher {
    void publishCardApplied(Long applicationId, Long userId, Long cardTypeId);
}
