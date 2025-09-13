package com.imagine.card.card_application.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.imagine.card.card_application.event.*;

public interface CardApplicationEventPublisher {

    // 유효성 실패
    void publishValidationFailed(ValidationFailedEvent event);
    // 신청 성공
    void publishCardRequested(CardApplicationRequestedEvent event);
    // 발급 완료
    void publishCardIssued(CardIssuedEvent event);
    // 상태 전환 (승인/거절 등)
    void publishStatusChanged(CardApplicationStatusChangedEvent event);

}
