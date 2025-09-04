package com.imagine.card.card_application.domain.repository;

import com.imagine.card.card_application.domain.model.CardType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CardTypeRepository extends JpaRepository<CardType,Long> {
    
    // 카드타입 id 로 조회
    Optional<CardType> findById(Long id);

    // 카드명 조회
    boolean existsByName(String name);

    // 카드 활성화 여부 조회
    boolean existsByIdAndIsActiveTrue(Long cardTypeId);

}
