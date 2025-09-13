package com.imagine.card.card_application.infrastructure.kafka;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.imagine.card.card_application.application.service.ApprovalService;
import com.imagine.card.card_application.application.service.IssuingService;
import com.imagine.card.card_application.domain.model.ApplicationStatus;
import com.imagine.card.card_application.domain.model.CardApplication;
import com.imagine.card.card_application.domain.model.EmailTemplate;
import com.imagine.card.card_application.event.CardApplicationRequestedEvent;
import com.imagine.card.card_application.event.CardApplicationStatusChangedEvent;
import com.imagine.card.card_application.event.CardIssuedEvent;
import com.imagine.card.card_application.event.ValidationFailedEvent;
import com.imagine.card.card_application.infrastructure.notification.EmailSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Objects;

/* Kafka 구현체 -- consumer */
@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final ApprovalService approvalService;
    private final IssuingService issuingService;
    private final EmailSender emailSender;
    private final ObjectMapper objectMapper;

    /**
     * 유효성 실패
     */
    @KafkaListener(topics = "card.application.failed", groupId="card-service-group")
    public void consumeValidationFailed(ValidationFailedEvent event) {
        log.info("Validation Failed Event 수신 : userId={}, cardTypeId={}, reason={}",
                event.getUserId(), event.getCardTypeId(), event.getReason());
        // TODO: 보안 로그 저장, CS 알림 처리
    }

    /**
     * 신청 성공
     */
    @KafkaListener(topics = "card.application.requested", groupId="card-service-group")
    //public void consumeCardRequested(CardApplicationRequestedEvent event) {
    public void consumeCardRequested(String payload) throws JsonProcessingException {
        CardApplicationRequestedEvent event = objectMapper.readValue(payload,CardApplicationRequestedEvent.class);
        // 심사 서비스 호출 -> 승인시 changed 발행
        approvalService.processApproval(event);
    }

    /**
     * 상태 전환 (심사거절 case) : 직접 알림서비스 호출
     */
    @KafkaListener(topics = "card.status.changed.judging", groupId="notification-service-group")
//    public void consumeStatusChangedForNotification(CardApplicationStatusChangedEvent event)  {
    public void consumeStatusChangedForNotification(String payload) throws JsonProcessingException {

        CardApplicationStatusChangedEvent event = objectMapper.readValue(payload,CardApplicationStatusChangedEvent.class);
        log.info("card.status.changed.judging / notification-service-group 이벤트 수신: ");

        CardApplication.ApplicationStatus status = event.getStatus();
        if (Objects.requireNonNull(status) == CardApplication.ApplicationStatus.REJECTED) {
            emailSender.send(
                    event.getEmail(),
                    EmailTemplate.REJECTED.getSubject(),
                    EmailTemplate.REJECTED.formatBody(event.getReason())
            );
        } else {
            log.info("알림 불 필요 status = {}", status);
        }
    }

    /**
     * 상태 전환 (심사승인 case) : 발급서비스 호출 -> issued 발행 -> 알림
     */
    @KafkaListener(topics = "card.status.changed.judging", groupId="issuing-service-group")
    //public void consumeStatusChangedForIssuing(CardApplicationStatusChangedEvent event)  {
    public void consumeStatusChangedForIssuing(String payload) throws JsonProcessingException {
        log.info("card.status.changed.judging / issuing-service-group 이벤트 수신: ");
        CardApplicationStatusChangedEvent event = objectMapper.readValue(payload,CardApplicationStatusChangedEvent.class);
        // 발급승인 상태 인 경우만 발급서비스 호출
        CardApplication.ApplicationStatus status = event.getStatus();
        if (status == CardApplication.ApplicationStatus.APPROVED) {
            issuingService.processIssuing(event);       // 발급서비스 호출 -> issued 발행
        } else {
            log.info("발급처리 생략 : event.getStatus()= {} " , event.getStatus());
        }
    }

    /**
     * 발급 완료 > 알림서비스 호출
     */
    @KafkaListener(topics = "card.issued", groupId="card-service-group")
//    public void consumeCardIssued(CardIssuedEvent event) {
    public void consumeCardIssued(String payload) throws JsonProcessingException {

        CardIssuedEvent event = objectMapper.readValue(payload,CardIssuedEvent.class);

        log.info("Card Issued Event 수신: appId={}, userId={}",
                event.getAppId(), event.getUserId());

        CardApplication.ApplicationStatus status = event.getStatus();
        if (Objects.requireNonNull(status) == CardApplication.ApplicationStatus.APPROVED) {
            emailSender.send(
                    event.getEmail(),
                    EmailTemplate.APPROVED.getSubject(),
                    EmailTemplate.APPROVED.formatBody(event.getReason())
            );
        }
    }

}
