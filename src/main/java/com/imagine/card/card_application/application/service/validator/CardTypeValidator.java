package com.imagine.card.card_application.application.service.validator;

import com.imagine.card.card_application.application.service.exception.DomainException;
import com.imagine.card.card_application.domain.model.CardApplication;
import com.imagine.card.card_application.domain.model.CardType;
import com.imagine.card.card_application.domain.repository.CardApplicationRepository;
import com.imagine.card.card_application.domain.repository.CardTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/* 카드 신청 -- 관리자 전용 > 유효성 체크 */

@Component
@RequiredArgsConstructor
public class CardTypeValidator {

    private final CardTypeRepository cardTypeRepository;
    private final CardApplicationRepository cardApplicationRepository;

    /**
     * 카드명 중복 체크 (등록)
     * */
    public void checkCardNameForCreate(String name) {
        if (cardTypeRepository.existsByName(name)) {
            throw new DomainException("이미 존재하는 카드 상품명입니다.");
        }
    }

    /**
     * 카드명 중복 체크 (수정)
     * */
    public void checkCardNameForUpdate(CardType cardType, String newName) {

        if (newName != null
           && !cardType.getName().equals(newName)
           && cardTypeRepository.existsByName(newName)
       ) {
           throw new DomainException("이미 존재하는 카드명입니다.");
       }
    }

    /**
     * 카드타입 여부 체크
     * */
    public CardType checkCardType(Long cardTypeId) {
        return cardTypeRepository.findById(cardTypeId)
                .orElseThrow(() -> new IllegalArgumentException("카드 타입 없음"));
    }

    /**
     * 카드 신청 건 조회
     * */
    public CardApplication checkCardApplication(Long applicationId) {
        return cardApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("해당 카드 신청 건 없음"));
    }

    /**
     * 카드 활성화 여부 확인
     * */
    public void checkIsActive(Long cardTypeId) {
        if (!cardTypeRepository.existsByIdAndIsActiveTrue(cardTypeId) ) {
            throw new DomainException("비활성화 된 카드 입니다.");
        }
    }
}
