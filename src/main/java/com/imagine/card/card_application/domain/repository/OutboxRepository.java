package com.imagine.card.card_application.domain.repository;

import com.imagine.card.card_application.domain.model.OutboxEvent;
import com.imagine.card.card_application.domain.model.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxRepository extends JpaRepository<OutboxEvent, Long> {
    // 상태값으로 이벤트 조회
    List<OutboxEvent> findByStatus(OutboxStatus status);
}
