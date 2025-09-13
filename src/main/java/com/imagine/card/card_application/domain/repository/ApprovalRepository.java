package com.imagine.card.card_application.domain.repository;

import com.imagine.card.card_application.domain.model.Approval;
import org.springframework.data.jpa.repository.JpaRepository;

/* 심사테이블 리파지토리 */
public interface ApprovalRepository extends JpaRepository<Approval, Long> {
}
