package com.imagine.card.card_application.infrastructure.kafka;

import com.imagine.card.card_application.application.service.CardApplicationEventPublisher;
import com.imagine.card.card_application.event.CardApplicationRequestedEvent;
import com.imagine.card.card_application.event.CardApplicationStatusChangedEvent;
import com.imagine.card.card_application.event.CardIssuedEvent;
import com.imagine.card.card_application.event.ValidationFailedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/* Kafka 구현체 -- producer */
@Service
@RequiredArgsConstructor
public class KafkaProducerService implements CardApplicationEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    // 유효성 실패
    @Override
    public void publishValidationFailed(ValidationFailedEvent event) {
        kafkaTemplate.send("card.application.failed", event);
    }

    // 신청 성공
    @Override
    public void publishCardRequested(CardApplicationRequestedEvent event) {
        kafkaTemplate.send("card.application.requested", event);
    }

    // 발급 완료
    @Override
    public void publishCardIssued(CardIssuedEvent event) {
        kafkaTemplate.send("card.issued", event);

    }

    // 상태 전환 (승인/거절 등)
    @Override
    public void publishStatusChanged(CardApplicationStatusChangedEvent event) {
        kafkaTemplate.send("card.status.changed", event);
    }
}
