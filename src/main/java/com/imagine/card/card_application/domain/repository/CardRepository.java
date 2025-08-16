package com.imagine.card.card_application.domain.repository;

import com.imagine.card.card_application.domain.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<Card,Long> {
}
