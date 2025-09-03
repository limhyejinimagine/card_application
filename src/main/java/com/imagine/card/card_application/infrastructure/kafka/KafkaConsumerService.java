package com.imagine.card.card_application.infrastructure.kafka;

import com.imagine.card.card_application.event.CardApplicationRequestedEvent;
import com.imagine.card.card_application.event.CardApplicationStatusChangedEvent;
import com.imagine.card.card_application.event.CardIssuedEvent;
import com.imagine.card.card_application.event.ValidationFailedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/* Kafka 구현체 -- consumer */
@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumerService {

    @KafkaListener(topics = "card.application.failed", groupId="card-service-group")
    public void consumeValidationFailed(ValidationFailedEvent event) {
        log.info("Validation Failed Event 수신 : userId={}, cardTypeId={}, reason={}",
                event.getUserId(), event.getCardTypeId(), event.getReason());
        // TODO: 보안 로그 저장, CS 알림 처리
    }

    @KafkaListener(topics = "card.application.requested", groupId="card-service-group")
    public void consumeCardRequested(CardApplicationRequestedEvent event) {
        log.info("Card Application Requested Event 수신: appId={}, userId={}, cardTypeId={}",
                event.getAppId(), event.getUserId(), event.getCardTypeId());
        // TODO: 심사 프로세스 전달
    }
    @KafkaListener(topics = "card.issued", groupId="card-service-group")
    public void consumeCardIssued(CardIssuedEvent event) {
        log.info("Card Issued Event 수신: appId={}, userId={}",
                event.getAppId(), event.getUserId());
        // TODO: 배송 연동, 고객 알림 서비스 호출
    }

    @KafkaListener(topics = "card.status.changed", groupId="card-service-group")
    public void consumeStatusChanged(CardApplicationStatusChangedEvent event) {
        log.info("Card Status Changed Event 수신 : appId={}, status={}",
                event.getAppId(), event.getStatus());
        // TODO: 상태 이력 저장, 리포트 서비스 전달
    }


}
