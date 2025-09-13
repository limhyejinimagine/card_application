package com.imagine.card.card_application.application.service;

import com.imagine.card.card_application.application.dto.*;
import com.imagine.card.card_application.application.service.common.JsonConverter;
import com.imagine.card.card_application.application.service.exception.DomainException;
import com.imagine.card.card_application.application.service.validator.CardTypeValidator;
import com.imagine.card.card_application.domain.model.ApplicationStatusHistory;
import com.imagine.card.card_application.domain.model.CardApplication;
import com.imagine.card.card_application.domain.model.CardType;
import com.imagine.card.card_application.domain.model.OutboxStatus;
import com.imagine.card.card_application.domain.repository.ApplicationStatusHistoryRepository;
import com.imagine.card.card_application.domain.repository.CardTypeRepository;
import com.imagine.card.card_application.domain.repository.OutboxRepository;
import com.imagine.card.card_application.event.CardApplicationStatusChangedEvent;
import com.imagine.card.card_application.domain.model.OutboxEvent;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;
import java.util.List;


/* 카드 상품관리 -- 관리자 전용 서비스 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CardTypeService {

    private final CardTypeValidator validator;
    private final CardTypeRepository cardTypeRepository;
    private final ApplicationStatusHistoryRepository historyRepository;
    private final OutboxRepository outboxRepository;
    private final JsonConverter jsonConverter;

    /**
     * 카드 상품 등록
     */
    @Transactional
    public CardTypeResponse registCardType (CreateCardTypeRequest dto) {

        // 1. 카드명 중복체크
        validator.checkCardNameForCreate(dto.name());

        // 2. 없으면 엔티티 생성
        var cardType = CardType.builder()
                .name(dto.name())
                .description(dto.description())
                .build();

        // 3. DB insert
        cardTypeRepository.save(cardType);

        // 4. 응답 DTO 반환
        return new CardTypeResponse(
                cardType.getId(),
                cardType.getName(),
                cardType.getDescription(),
                cardType.getIsActive()
        );

    }

    /**
     * 카드 상품 목록 조회
     */
    @Transactional(readOnly = true)
    public List<CardTypeResponse> getCardTypeList() {
        log.info(">>> Transaction active={}, readOnly={}",
                TransactionSynchronizationManager.isActualTransactionActive(),
                TransactionSynchronizationManager.isCurrentTransactionReadOnly());


        return cardTypeRepository.findAll().stream()
                .map(cardType -> new CardTypeResponse(cardType.getId(), cardType.getName(), cardType.getDescription(), cardType.getIsActive()))
                .toList();
    }

    /**
     * 카드 상품 단건 조회
     */
    @Transactional(readOnly = true)
    public CardTypeResponse getCardTypeOne(Long id) {
        CardType cardType = validator.checkCardType(id);
        return CardTypeResponse.from(cardType);
    }

    /**
     * 카드 상품 수정
     */
    @Transactional
    public void updateCardType(Long id, UpdateCardTypeRequest req) {

        log.info("DTO={}", req);
        log.info("req.isActive={}", req.isActive());

        // 1. 수정할 대상 엔티티 조회
        CardType cardType = validator.checkCardType(id);

        // 2. 이름 중복 체크
        validator.checkCardNameForUpdate(cardType,req.name());

        // 3. 수정
        if (req.name() != null) {
            cardType.setName(req.name());
        }
        if (req.description() != null) {
            cardType.setDescription(req.description());
        }
        if (req.isActive() != null) {
            cardType.setIsActive(req.isActive());
        }
    }

    /**
     * 카드 신청 상태 변경 (승인/거절 등)
     * */
    @Transactional
    public void ChangeApplicationStatus(@Valid UpdateApplicationStatusRequest req) {
        System.out.println("######ChangeApplicationStatus CALL ############");

        // 1. 신청 건 조회 : 엔티티 조회 > ***영속성 컨텍스트에 엔티티 올라감
        CardApplication ca = validator.checkCardApplication(req.getApplicationId());

        // 2. 해당 카드 활성화 여부 체크
        validator.checkIsActive(ca.getCardType().getId());

        // 3. 상태 전이 규칙 검증 : APPROVED(REJECTED) → 다시 REJECTED(APPROVED) 불가
        CardApplication.ApplicationStatus reqStatus     = req.getStatus();
        CardApplication.ApplicationStatus applyStatus   = ca.getStatus();

        if (applyStatus == CardApplication.ApplicationStatus.APPROVED
                && reqStatus == CardApplication.ApplicationStatus.REJECTED) {
            throw new DomainException("승인된 신청 건은 거절할 수 없습니다.");
        };

        if (applyStatus == CardApplication.ApplicationStatus.REJECTED
                && reqStatus == CardApplication.ApplicationStatus.APPROVED) {
            throw new DomainException("거절된 신청 건은 승인할 수 없습니다.");
        };

        // 4. 상태 변경
        ca.setStatus(reqStatus);                // Dirty Checking
        ca.setRejectionReason(req.getMessage());

        // 5. 상태변경 이력 저장 : DB 에 없는 새로운 행이므로 영속상태가 아니기 때문에 엔티티생성
        var history = ApplicationStatusHistory.builder()
                .application(ca)
                .status(req.getStatus())
                .message(req.getMessage())
                .changedAt(LocalDateTime.now())
                .build();
        historyRepository.save(history);    // 반드시 save() 해야 JPA가 관리

        // 6. Kafka 발행을 위한 Outbox 에 이벤트 저장 (status:NEW)
        OutboxEvent event = OutboxEvent.builder()
                .aggregateId(ca.getId())
                .aggregateType("CardType")
                .type("card.status.changed.admin")
                .payload(jsonConverter.toJson(new CardApplicationStatusChangedEvent(ca)))
                .status(OutboxStatus.NEW)
                .build();
        outboxRepository.save(event);

        // 7. 커밋 시 Optimistic Lock 충돌 여부 확인 - 성공 → 정상 반영 |  실패 → "이미 처리된 신청 건" 에러(GlobalException)

    }

}
