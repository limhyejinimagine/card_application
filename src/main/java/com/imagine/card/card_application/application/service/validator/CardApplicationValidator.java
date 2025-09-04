package com.imagine.card.card_application.application.service.validator;

import com.imagine.card.card_application.application.service.exception.DomainException;
import com.imagine.card.card_application.domain.model.CardType;
import com.imagine.card.card_application.domain.model.User;
import com.imagine.card.card_application.domain.repository.CardApplicationRepository;
import com.imagine.card.card_application.domain.repository.CardTypeRepository;
import com.imagine.card.card_application.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


/* 카드 신청 -- 사용자 전용 > 유효성 체크 */
@Component
@RequiredArgsConstructor
public class CardApplicationValidator {

    private final UserRepository userRepository;
    private final CardTypeRepository cardTypeRepository;
    private final CardApplicationRepository applicationRepository;


    public ValidationResult validateAll(Long userId, Long cardTypeId) {

        // 1. 중복신청 체크
        if (applicationRepository.existsByUser_IdAndCardType_Id(userId, cardTypeId) ) {
            throw new DomainException("이미 신청된 카드 타입입니다.");
        }
        
        // 2. 유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DomainException("사용자를 찾을 수 없습니다."));
        // 3. 카드타입 조회
        CardType cardType = cardTypeRepository.findById(cardTypeId)
                .orElseThrow(() -> new DomainException("존재하지 않는 카드 타입입니다."));

        boolean  isActive = cardTypeRepository.existsByIdAndIsActiveTrue(cardTypeId);
        System.out.println("카드 활성화 여부 isActive="+isActive);

        // 4. 카드 활성화 여부 확인
        if (!cardTypeRepository.existsByIdAndIsActiveTrue(cardTypeId) ) {
            throw new DomainException("비활성화 된 카드 입니다.");
        }
        
        // 5. 검증 결과 묶어서 반환
        return ValidationResult.builder()
                .user(user)
                .cardType(cardType)
                .build();
    }
}
