package com.imagine.card.card_application.application.service;

import com.imagine.card.card_application.application.service.common.JsonConverter;
import com.imagine.card.card_application.application.service.exception.BusinessException;
import com.imagine.card.card_application.domain.model.*;
import com.imagine.card.card_application.domain.repository.*;
import com.imagine.card.card_application.event.CardApplicationRequestedEvent;
import com.imagine.card.card_application.event.CardApplicationStatusChangedEvent;
import com.imagine.card.card_application.event.CardIssuedEvent;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.boot.model.naming.IllegalIdentifierException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/*  카드 발급 서비스   */
@Service
@Slf4j
@RequiredArgsConstructor
public class IssuingService {

    private final CardRepository cardRepository;
    private final CardApplicationRepository applicationRepository;
    private final ApplicationStatusHistoryRepository historyRepository;
    private final OutboxRepository outboxRepository;
    private final JsonConverter jsonConverter;


    @Transactional
    public void processIssuing(CardApplicationStatusChangedEvent event) {

        // 1. 카드 번호 추출
        CardApplication ca = applicationRepository.findById(event.getAppId())
                .orElseThrow(() -> new EntityNotFoundException("Application not found"));

        Card card = issueCard(ca);

        // 2. 카드 발급 테이블 저장
        cardRepository.save(card);

        // 3. 이력테이블 저장
        ApplicationStatusHistory history = ApplicationStatusHistory.builder()
                .application(ca)
                .status(CardApplication.ApplicationStatus.ISSUED)
                .changedAt(LocalDateTime.now())
                .message("카드발급완료")
                .build();
        historyRepository.save(history);


        // 4. Outbox 테이블 저장
        CardIssuedEvent issuedEvent = CardIssuedEvent.builder()
                .appId(ca.getId())
                .userId(ca.getUser().getId())
                .cardNumber(card.getCardNumber())
                .status(event.getStatus())
                .email(event.getEmail())
                .reason(event.getReason())
                .build();

        OutboxEvent oe = OutboxEvent.builder()
                .aggregateType("CardApplication")
                .aggregateId(ca.getId())
                .type("card.issued")
                .payload(jsonConverter.toJson(issuedEvent))
                .status(OutboxStatus.NEW)
                .build();
        outboxRepository.save(oe);
    }

    // 더미 카드번호 생성
    public Card issueCard(CardApplication application) {
        String dummyCardNumber = "1234-5678-9000" + (int)(Math.random() * 10000);
        return new Card(application,dummyCardNumber, LocalDateTime.now());
    }
}
