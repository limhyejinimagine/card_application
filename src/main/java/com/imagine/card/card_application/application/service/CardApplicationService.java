package com.imagine.card.card_application.application.service;


import com.imagine.card.card_application.application.dto.ApplyCardRequest;
import com.imagine.card.card_application.application.dto.ApplyCardResponse;
import com.imagine.card.card_application.application.service.common.JsonConverter;
import com.imagine.card.card_application.application.service.validator.CardApplicationValidator;
import com.imagine.card.card_application.application.service.validator.ValidationResult;
import com.imagine.card.card_application.config.aop.WithRedisLock;
import com.imagine.card.card_application.domain.model.*;
import com.imagine.card.card_application.domain.repository.ApplicationStatusHistoryRepository;
import com.imagine.card.card_application.domain.repository.CardApplicationRepository;

import com.imagine.card.card_application.domain.repository.OutboxRepository;
import com.imagine.card.card_application.event.CardApplicationRequestedEvent;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
@Slf4j
@RequiredArgsConstructor
public class CardApplicationService {

    private final CardApplicationValidator validator;
    private final CardApplicationRepository applicationRepository;
    private final ApplicationStatusHistoryRepository historyRepository;
    private final OutboxRepository outboxRepository;
    private final JsonConverter jsonConverter;

    /**
     * 카드 신청 메인 유스케이스
     *  - 기초 검증: 카드종류 활성/중복신청 체크
     * -  Redis 분산락으로 중복 클릭/요청 방지
     * -  신청 저장 + 상태이력 기록
     * -  kafka 이벤트 발행 (outbox 패턴적용)
     */

    // Redis Lock AOP 적용 (SpEL 로 Key 생성 규칙 정의)
    @WithRedisLock(
        key = "'lock:user:' + #dto.userId + ':cardType:' + #dto.cardTypeId",
        timeout = 5
    )
    @Transactional
    public ApplyCardResponse apply(ApplyCardRequest dto) {

        // 1. 기초검증
        ValidationResult result = validator.validateAll(dto.userId(), dto.cardTypeId());
        User user = result.getUser();
        CardType cardType = result.getCardType();

        try {
            // 2. 신청 저장
            var app = saveApply(user, cardType);

            // 3. 상태 이력 저장
            saveHistory(app);

            // 4. kafka 이벤트 발행을 위한 outbox 에 이벤트 저장 (NEW)
            OutboxEvent event = OutboxEvent.builder()
                                .aggregateId(app.getId())
                                .aggregateType("CardApplication")
                                .type("card.application.requested")
                                .payload(jsonConverter.toJson(new CardApplicationRequestedEvent(app)))
                                .status(OutboxStatus.NEW)
                                .build();

            outboxRepository.save(event);

            log.info("DB 저장 성공 {}", app.getId());
            return new ApplyCardResponse(app.getId());
        } catch (Exception e) {
            log.error("카드 신청 처리 실패: {}", e.getMessage(), e);
            throw e;
        }


    }

    /**
     * 신청 저장
     */
    private CardApplication saveApply(User user, CardType cardType) {
        var now = LocalDateTime.now();
        var app = CardApplication.builder()
                .user(user)
                .cardType(cardType)
                .status(CardApplication.ApplicationStatus.REQUESTED)
                .requestedAt(now)
                .build();
        applicationRepository.saveAndFlush(app);
        return app;
    }

    /**
     * 상태 이력 저장
     */
    private void saveHistory (CardApplication app) {
        var now = LocalDateTime.now();
        var history = ApplicationStatusHistory.builder()
                .application(app)
                .status(CardApplication.ApplicationStatus.REQUESTED)
                .changedAt(now)
                .message("신청 접수")
                .build();
        historyRepository.save(history);
    }
}
