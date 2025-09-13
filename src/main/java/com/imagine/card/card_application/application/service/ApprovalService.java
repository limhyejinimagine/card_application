package com.imagine.card.card_application.application.service;

import com.imagine.card.card_application.application.service.common.JsonConverter;
import com.imagine.card.card_application.domain.model.*;
import com.imagine.card.card_application.domain.repository.ApprovalRepository;
import com.imagine.card.card_application.domain.repository.CardApplicationRepository;
import com.imagine.card.card_application.domain.repository.OutboxRepository;
import com.imagine.card.card_application.domain.repository.UserRepository;
import com.imagine.card.card_application.event.CardApplicationRequestedEvent;
import com.imagine.card.card_application.event.CardApplicationStatusChangedEvent;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/* 카드 심사 서비스 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ApprovalService {

    private final ApprovalRepository approvalRepository;
    private final OutboxRepository outboxRepository;
    private final UserRepository userRepository;
    private final JsonConverter jsonConverter;
    private final CardApplicationRepository cardApplicationRepository;

    /**
     * 심사 진행
     * */
    @Transactional
    public void processApproval(CardApplicationRequestedEvent reqEvent) {

        // 1. 심사규칙 검증 (Mock 로직)
        boolean approved = mockApprovalRule(reqEvent.getUserId());

        Long appId = reqEvent.getAppId();

        CardApplication ca = cardApplicationRepository.findById(appId)
                .orElseThrow(() -> new EntityNotFoundException("Application not found"));

        // 2. Approval 엔티티 저장
        User user = userRepository.findById(reqEvent.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Approval approval = new Approval(
                ca,
                user ,
                approved ? Approval.ApprovalResult.APPROVED : Approval.ApprovalResult.REJECTED,
                123,
                LocalDateTime.now()
        );
        approvalRepository.save(approval);


        // 3. 결과 이벤트 생성 & Outbox 저장
        CardApplicationStatusChangedEvent changedEvent = CardApplicationStatusChangedEvent.builder()
                .appId(reqEvent.getAppId())
                .status(approved ? CardApplication.ApplicationStatus.APPROVED : CardApplication.ApplicationStatus.REJECTED)
                .reason(approved ? null : "모의심사 실패")
                .email(user.getEmail()) // TODO: 이메일 null 일 경우 Outbox 에 FAILED 처리 || 이벤트 생성 스킵
                .build();

        OutboxEvent oe = OutboxEvent.builder()
                        .aggregateType("CardApplication")
                        .aggregateId(changedEvent.getAppId())
                        .type("card.status.changed.judging")
                        .payload(jsonConverter.toJson(changedEvent))
                        .status(OutboxStatus.NEW)
                        .build();

        outboxRepository.save(oe);
    }

    private boolean mockApprovalRule(Long userId) {
        return userId % 2 == 0;
    }

}
