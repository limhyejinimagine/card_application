package com.imagine.card.card_application.domain.repository;

import com.imagine.card.card_application.domain.model.CardApplication;
import com.imagine.card.card_application.domain.model.CardType;
import com.imagine.card.card_application.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CardApplicationRepository extends JpaRepository<CardApplication,Long> {

    // 연관관계 필드이므로 _ 로 id 접근
    boolean existsByUser_IdAndCardType_Id(Long userId, Long cardTypeId);
}
