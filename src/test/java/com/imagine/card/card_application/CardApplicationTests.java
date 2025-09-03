package com.imagine.card.card_application;

import com.imagine.card.card_application.application.dto.ApplyCardRequest;
import com.imagine.card.card_application.application.dto.ApplyCardResponse;
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

import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

/* Real Redis 테스트 */

@SpringBootTest
@Slf4j
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CardApplicationTests {

	@Autowired RedisLockManager redisLockManager;
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

	/* 단일 요청 정상 동작 테스트 */
	@Test
	void 카드신청_정상처리() {
		// given
		User u = userRepository.save(User.builder()
				.name("단일요청_" + UUID.randomUUID())
				.phone("010-" + new Random().nextInt(10000))
				.build());

		CardType ct = cardTypeRepository.save(CardType.builder()
				.name("단일카드_" + UUID.randomUUID())
				.isActive(true)
				.build());


		ApplyCardRequest req = new ApplyCardRequest(u.getId(), ct.getId());

		// when
		ApplyCardResponse res = cardApplicationService.apply(req);

		// then
		assertNotNull(res);
	}

	/* 동시 요청(락 동작) 테스트  :
		실제 Redis 띄워서, 멀티 스레드로 같은 요청 두 번 날려서 한쪽만 성공하는지 검증 */
	@Test
	void 동시에_두번신청_하나는실패해야함() {
		// given
		ApplyCardRequest req = new ApplyCardRequest(user.getId(), cardType.getId());

		ExecutorService executor = Executors.newFixedThreadPool(2);

		// when
		Future<ApplyCardResponse> f1 = executor.submit(() -> cardApplicationService.apply(req));
		Future<ApplyCardResponse> f2 = executor.submit(() -> cardApplicationService.apply(req));

		int successCnt = 0;
		int failCnt = 0;

		for (Future<ApplyCardResponse> f : List.of(f1, f2)) {
			try{
				ApplyCardResponse res = f.get();	// 실행결과 기다림 (checked Exception 가능)
				if (res != null) successCnt++;

			} catch(ExecutionException e) {
				// 내부 작업에서 발생한 예외
				Throwable cause = e.getCause();
				if (cause instanceof IllegalStateException &&
					"이미 처리중입니다.".equals(cause.getMessage())) {
					failCnt++;
				} else {
					throw new RuntimeException("예상치 못한 예외 발생", e);
				}

			} catch (InterruptedException e) {
				// 인터럽트 상태 복구
				Thread.currentThread().interrupt();
				throw new RuntimeException("스레드 인터럽트 발생" , e);
			}
		}

		// then
		log.info("successCnt = " + successCnt);
		log.info("failCnt = " + failCnt);

		assertEquals(1,successCnt);	// 하나는 성공
		assertEquals(1,failCnt);	// 하나는 실패
		executor.shutdown();
	}


}
