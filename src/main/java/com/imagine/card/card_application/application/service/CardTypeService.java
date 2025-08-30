package com.imagine.card.card_application.application.service;

import com.imagine.card.card_application.application.dto.CardTypeResponse;
import com.imagine.card.card_application.application.dto.CreateCardTypeRequest;
import com.imagine.card.card_application.application.dto.UpdateCardTypeRequest;
import com.imagine.card.card_application.application.service.exception.DomainException;
import com.imagine.card.card_application.domain.model.CardType;
import com.imagine.card.card_application.domain.repository.CardTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/* 카드 상품관리 -- 관리자 전용 서비스 */
@Service
@RequiredArgsConstructor
public class CardTypeService {

    private final CardTypeRepository cardTypeRepository;

    /**
     * 카드 상품 등록
     */
    @Transactional
    public CardTypeResponse registCardType (CreateCardTypeRequest dto) {
        
        // 1. 카드명 중복체크
        if (cardTypeRepository.existsByName(dto.name())) {
            throw new DomainException("이미 존재하는 카드 상품명입니다.");
        }

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

   // TODO : MASTER-SLAVE 분리>> Spring + RoutingDataSource로 구현
    /**
     * 카드 상품 목록 조회
     */
    @Transactional(readOnly = true)
    public List<CardTypeResponse> getCardTypeList() {
        return cardTypeRepository.findAll().stream()
                .map(cardType -> new CardTypeResponse(cardType.getId(), cardType.getName(), cardType.getDescription(), cardType.getIsActive()))
                .toList();
    }

    /**
     * 카드 상품 단건 조회
     */
    @Transactional(readOnly = true)
    public CardTypeResponse getCardTypeOne(Long id) {
        CardType cardType = cardTypeRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("카드 타입 없음"));
        return CardTypeResponse.from(cardType);
    }

    /**
     * 카드 상품 수정
     */
    @Transactional
    public void updateCardType(Long id, UpdateCardTypeRequest req) {

        // 1. 수정할 대상 엔티티 조회
        CardType cardType = cardTypeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("카드 타입 없음"));

        // 2. 이름 중복 체크
        if ( req.name() != null
              &&  !cardType.getName().equals(req.name())
        && cardTypeRepository.existsByName(req.name()))
         {
            throw new IllegalArgumentException("이미 존재하는 카드명입니다.");
        }

        // 3. 수정`
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
