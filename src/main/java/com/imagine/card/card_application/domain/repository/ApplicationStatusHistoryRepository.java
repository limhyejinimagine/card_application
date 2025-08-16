package com.imagine.card.card_application.domain.repository;

import com.imagine.card.card_application.domain.model.ApplicationStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationStatusHistoryRepository extends JpaRepository<ApplicationStatusHistory,Long> {
}
