package com.imagine.card.card_application.application.service;


import com.imagine.card.card_application.application.dto.ApplyCardRequest;
import com.imagine.card.card_application.application.dto.ApplyCardResponse;
import com.imagine.card.card_application.application.service.validator.CardApplicationValidator;
import com.imagine.card.card_application.application.service.validator.ValidationResult;
import com.imagine.card.card_application.config.aop.WithRedisLock;
import com.imagine.card.card_application.domain.model.*;
import com.imagine.card.card_application.domain.repository.ApplicationStatusHistoryRepository;
import com.imagine.card.card_application.domain.repository.CardApplicationRepository;

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
    private final CardApplicationEventPublisher eventProducer;

    /**
     * 카드 신청 메인 유스케이스
     *  - 기초 검증: 카드종류 활성/중복신청 체크
     * -  Redis 분산락으로 중복 클릭/요청 방지
     * -  신청 저장 + 상태이력 기록
     * -  kafka 이벤트 발행
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

            // 4. 신청 성공 시 kafka 이벤트 발행
            // TODO: afterCommit을 이용해서 “DB 트랜잭션 커밋 성공 후에만 Kafka 발행”
            eventProducer.publishCardRequested(
                    CardApplicationRequestedEvent.builder()
                            .appId(app.getId())
                            .userId(app.getUser().getId())
                            .cardTypeId(cardType.getId())
                            .build()
            );

            log.info("DB 저장 성공 {}", app.getId());
            return new ApplyCardResponse(app.getId());
        } catch (Exception e) {
            log.error("카드 신청 처리 실패: {}", e.getMessage(), e);
            throw e;
        }


    }

    /*
    @Transactional
    public ApplyCardResponse apply(ApplyCardRequest dto) {

        // 1. 기초검증
        ValidationResult result = validator.validateAll(dto.userId(), dto.cardTypeId());
        User user = result.getUser();
        CardType cardType = result.getCardType();

        // 2. Redis 분산락으로 중복 클릭/ 요청 방지
        String key = "lock:user:" + dto.userId() + ":cardType:" + dto.cardTypeId();
        String lockVal = lockManger.tryLock(key, 5);

        log.info("key= " + ", lockVal= " + lockVal);

        // 락을 못 잡은 경우
        if (lockVal == null) {
            log.warn("Redis Lock 실패: userId={}, cardTypeId={}", dto.userId(), dto.cardTypeId());
            eventProducer.publishValidationFailed(
                    ValidationFailedEvent.builder()
                            .userId(dto.userId())
                            .cardTypeId(dto.cardTypeId())
                            .reason("중복신청 / 락 충돌")
                            .build()
            );
            throw new IllegalStateException("이미 처리중입니다.");
        }

        try {
            // 3. 신청 저장
            var app = saveApply(user, cardType);

            // 4. 상태 이력 저장
            saveHistory(app);

            // 5. kafka 이벤트 발행 : 신청 성공
            eventProducer.publishCardRequested(
                    CardApplicationRequestedEvent.builder()
                            .appId(app.getId())
                            .userId(app.getUser().getId())
                            .cardTypeId(cardType.getId())
                            .build()
            );

            return new ApplyCardResponse(app.getId());
        }catch (DataAccessException e) {
            log.error("DB 오류: {}", e.getMessage(), e);
            throw new RuntimeException("DB 오류 발생", e);
        }finally{
            lockManger.unlock(key,lockVal);
        }
    }
*/
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

/*
    @Transactional
    public ApplyCardResponse apply_old (ApplyCardRequest dto) {

        // 1. 기초검증
        ValidationResult result = validator.validateAll(dto.userId(), dto.cardTypeId());
        User user = result.getUser();
        CardType cardType = result.getCardType();

        // TODO
        // 2. Redis 분산락으로 중복 클릭/ 요청 방지

        // 3. 신청 저장
        var now = LocalDateTime.now();
        var app = CardApplication.builder()
                .user(user)
                .cardType(cardType)
                .status(CardApplication.ApplicationStatus.REQUESTED)
                .requestedAt(now)
                .build();
        applicationRepository.save(app);

        // 4. 상태 이력 저장
        var history = ApplicationStatusHistory.builder()
                .application(app)
                .status(CardApplication.ApplicationStatus.REQUESTED)
                .changedAt(now)
                .message("신청 접수")
                .build();
        historyRepository.save(history);

        // TODO ::: (지금은 일단 No-Op)
        // 5. kafka 이벤트
        eventProducer.publishCardApplied(app.getId(), user.getId(), cardType.getId());

        return new ApplyCardResponse(app.getId());
    }
*/
}
