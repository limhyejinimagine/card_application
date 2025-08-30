package com.imagine.card.card_application.application.service;


import com.imagine.card.card_application.application.dto.ApplyCardRequest;
import com.imagine.card.card_application.application.dto.ApplyCardResponse;
import com.imagine.card.card_application.application.service.exception.DomainException;
import com.imagine.card.card_application.domain.model.ApplicationStatusHistory;
import com.imagine.card.card_application.domain.model.CardApplication;
import com.imagine.card.card_application.domain.repository.ApplicationStatusHistoryRepository;
import com.imagine.card.card_application.domain.repository.CardApplicationRepository;
import com.imagine.card.card_application.domain.repository.CardTypeRepository;
import com.imagine.card.card_application.domain.repository.UserRepository;
import com.imagine.card.card_application.infrastructure.kafka.CardApplicationEventPublisher;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class CardApplicationService {

    private final UserRepository userRepository;
    private final CardTypeRepository cardTypeRepository;
    private final CardApplicationRepository applicationRepository;
    private final ApplicationStatusHistoryRepository historyRepository;
    private final CardApplicationEventPublisher eventProducer;

    /**
     * 카드 신청 메인 유스케이스
     * - Redis 분산락으로 중복 클릭/요청 방지
     * - 기초 검증: 카드종류 활성/중복신청 체크
     * - 신청 저장 + 상태이력 기록
     */

    @Transactional
    public ApplyCardResponse apply(ApplyCardRequest dto) {

        // 1. 중복신청 체크
        if (applicationRepository.existsByUser_IdAndCardType_Id(dto.userId(), dto.cardTypeId())) {
            throw new DomainException("이미 신청된 카드 타입입니다.");
        }

        // 2. 카드타입 활성 체크 + 유저/카드타입 로드
        var user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new DomainException("사용자를 찾을 수 없습니다."));
        var cardType = cardTypeRepository.findById(dto.cardTypeId())
                .orElseThrow(() -> new DomainException("존재하지 않는 카드 타입입니다."));

        if (!cardType.getIsActive()) {
            throw new DomainException("비활성화된 카드 타입입니다.");
        }

        // 3. 신청 저장
        var now = LocalDateTime.now();
        var app = CardApplication.builder()
                .user(user)
                .cardType(cardType)
                .status(CardApplication.ApplicationStatus.REQUESTED)
                .requestedAt(now)
                .build();
        applicationRepository.save(app);

        // 4. 상태 이력
        var history = ApplicationStatusHistory.builder()
                .application(app)
                .status(CardApplication.ApplicationStatus.REQUESTED)
                .changedAt(now)
                .message("신청 접수")
                .build();
        historyRepository.save(history);

        // 5. 이벤트 (지금은 일단 No-Op)
        eventProducer.publishCardApplied(app.getId(), user.getId(), cardType.getId());

        return new ApplyCardResponse(app.getId());
    }
}
