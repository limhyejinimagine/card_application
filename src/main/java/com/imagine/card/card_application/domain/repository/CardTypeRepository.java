package com.imagine.card.card_application.domain.repository;

import com.imagine.card.card_application.domain.model.CardType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CardTypeRepository extends JpaRepository<CardType,Long> {
    Optional<CardType> findByIdAndIsActiveTrue(Long id);

}
