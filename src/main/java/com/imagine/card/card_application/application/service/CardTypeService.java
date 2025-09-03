package com.imagine.card.card_application.application.service;

import com.imagine.card.card_application.application.dto.CardTypeResponse;
import com.imagine.card.card_application.application.dto.CreateCardTypeRequest;
import com.imagine.card.card_application.application.dto.UpdateCardTypeRequest;
import com.imagine.card.card_application.application.service.validator.CardTypeValidator;
import com.imagine.card.card_application.domain.model.CardType;
import com.imagine.card.card_application.domain.repository.CardTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;

/* 카드 상품관리 -- 관리자 전용 서비스 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CardTypeService {

    private final CardTypeValidator validator;
    private final CardTypeRepository cardTypeRepository;

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



}
