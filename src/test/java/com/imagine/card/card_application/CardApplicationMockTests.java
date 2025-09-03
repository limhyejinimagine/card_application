package com.imagine.card.card_application;

import com.imagine.card.card_application.application.dto.ApplyCardRequest;
import com.imagine.card.card_application.application.service.CardApplicationService;
import com.imagine.card.card_application.domain.model.CardType;
import com.imagine.card.card_application.domain.model.User;
import com.imagine.card.card_application.domain.repository.CardTypeRepository;
import com.imagine.card.card_application.domain.repository.UserRepository;
import com.imagine.card.card_application.infrastructure.redisLock.RedisLockManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;


/* Mock 테스트 */

@SpringBootTest
@Slf4j
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CardApplicationMockTests {

    @Autowired private CardApplicationService cardApplicationService;
    @Autowired UserRepository userRepository;
    @Autowired CardTypeRepository cardTypeRepository;

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

    /* Redis 없는 환경 (Mocking) :
        RedisLockManager가 락을 못 잡은 상황을 강제로 흉내내는(Mock) 케이스 */
    @MockitoBean
    private RedisLockManager redisLockManager;

    @Test
    void 락못잡으면_예외던짐() {
        // given : tryLock() 호출 시 무조건 null 반환
        when(redisLockManager.tryLock(anyString(),anyLong()))
                .thenReturn(null);

        // when & then : 락 못 잡았으므로 예외 발생해야 함
        ApplyCardRequest req = new ApplyCardRequest(user.getId(), cardType.getId());
        assertThrows(IllegalStateException.class, () -> cardApplicationService.apply(req));
    }


}
