package com.imagine.card.card_application;

import com.imagine.card.card_application.application.dto.UpdateApplicationStatusRequest;
import com.imagine.card.card_application.application.service.CardApplicationService;
import com.imagine.card.card_application.application.service.CardTypeService;
import com.imagine.card.card_application.domain.model.CardApplication;
import com.imagine.card.card_application.domain.model.CardType;
import com.imagine.card.card_application.domain.model.User;
import com.imagine.card.card_application.domain.repository.CardApplicationRepository;
import com.imagine.card.card_application.domain.repository.CardTypeRepository;
import com.imagine.card.card_application.domain.repository.UserRepository;
import com.imagine.card.card_application.event.CardApplicationStatusChangedEvent;
import jakarta.persistence.OptimisticLockException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

/* 관리자 카드상태 변경 : 낙관적 락 + 상태 변경 + Outbox 저장 + 예외 처리 통합테스트 */
@SpringBootTest
@Slf4j
@ActiveProfiles("test")
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class CardTypeServiceTest {

    @Autowired
    CardApplicationRepository repostiroy;

    @Autowired
    CardTypeService service;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CardTypeRepository cardTypeRepository;

    private User user;
    private CardType cardType;

    // fixture
    @BeforeEach
    void setUp() {
        user = userRepository.save(User.builder()
                .name("테스트유저_" + UUID.randomUUID())
                .phone("010-1234-" + new Random().nextInt(10000))
                .build());

        cardType = cardTypeRepository.save(CardType.builder()
                .name("테스트카드_" + UUID.randomUUID())
                .isActive(true)
                .build());
        log.info(">>> cardTypeId = " + cardType.getId());
    }

    // 동일한 신청 건을 두 스레드에서 동시에 수정하도록 해서
    // 한쪽은 성공, 다른 한쪽은 OptimisticLockException 으로 실패하는 걸 확인
    @Test
    void 동시에_승인하면_OptimisticLock예외_발생() throws ExecutionException, InterruptedException {
        // given
        CardApplication app = repostiroy.saveAndFlush(      // 여기서 flush → version 충돌 감지
                CardApplication.builder()
                        .status(CardApplication.ApplicationStatus.ISSUED)
                        .cardType(cardType)
                        .user(user)
                        .build());
        // when
        CountDownLatch latch = new CountDownLatch(1);

        Runnable task1 = () -> {
            try {
                latch.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            service.ChangeApplicationStatus(
                    new UpdateApplicationStatusRequest(app.getId(), CardApplication.ApplicationStatus.APPROVED,null)
            );
        };

        Runnable task2 = () -> {
            try {
                latch.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            service.ChangeApplicationStatus(
                    new UpdateApplicationStatusRequest(app.getId(), CardApplication.ApplicationStatus.APPROVED,null)
            );
        };

        ExecutorService executor = Executors.newFixedThreadPool(2);
        Future<?> f1 = executor.submit(task1);
        Future<?> f2 = executor.submit(task2);

        // 두 스레드 동시에 시작
        latch.countDown();

        // then


        // 성공한 스레드도 실제로 실행시키고 로그에 update가 찍히도록
        f1.get();

        // f2.get() 실행시 내부적으로 OptimisticLockException 발생
        ExecutionException ex = assertThrows(ExecutionException.class, f2::get);
        Throwable cause = ex.getCause();
        assertTrue(
                cause instanceof ObjectOptimisticLockingFailureException ||
                        cause instanceof OptimisticLockException
        );
        executor.shutdown();

    }
}
