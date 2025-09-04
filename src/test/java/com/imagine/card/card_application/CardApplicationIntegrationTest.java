package com.imagine.card.card_application;

import com.imagine.card.card_application.application.dto.ApplyCardRequest;
import com.imagine.card.card_application.application.service.CardApplicationService;
import com.imagine.card.card_application.domain.model.CardType;
import com.imagine.card.card_application.domain.model.User;
import com.imagine.card.card_application.domain.repository.CardApplicationRepository;
import com.imagine.card.card_application.domain.repository.CardTypeRepository;
import com.imagine.card.card_application.domain.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Propagation;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import javax.sql.DataSource;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

/*  Redis 락 + Kafka 이벤트 발행 + DB insert  통합 테스트  */

@Testcontainers
@Slf4j
@Transactional(propagation = Propagation.NOT_SUPPORTED) // 롤백 방지 (기초데이터유지)
@SpringBootTest
@ActiveProfiles("test")
public class CardApplicationIntegrationTest {

    // 1) MySQL 컨테이너
    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("card_db_test")
            .withUsername("test")
            .withPassword("test");

    // 2) Kafka 컨테이너
    @Container
    static KafkaContainer kafka = new KafkaContainer(
            DockerImageName.parse("apache/kafka:3.7.0")
    );

    // 3) Redis 컨테이너
    @Container
    static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:7.2"))
            .withExposedPorts(6379);

    // 4) SpringBoot 에 동적으로 컨테이너 정보 주입
    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        // MySQL
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);

        // Kafka
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);

        // Redis
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
    }

    @Autowired
    private CardApplicationService service;

    @Autowired
    private CardApplicationRepository repository;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate; // 실제 KafkaTemplate

    @Autowired private CardApplicationService cardApplicationService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    CardTypeRepository cardTypeRepository;

    @Autowired
    DataSource dataSource;

    private Long userId;
    private Long cardTypeId;

    // fixture
    @BeforeEach
    void setUp() {
        repository.deleteAll();
        userRepository.deleteAll();
        cardTypeRepository.deleteAll();

        User user = userRepository.save(
                User.builder()
                        .name("홍길동")
                        .phone("01012345678")
                        .ci("ci-test")
                        .build()
        );

        CardType cardType = cardTypeRepository.save(
                CardType.builder()
                        .name("체크카드")
                        .description("기본 테스트 카드")
                        .isActive(true)
                        .build()
        );

        this.userId = user.getId();
        this.cardTypeId = cardType.getId();

        System.out.println(">>> userId=" + userId + ", cardTypeId=" + cardTypeId);
    }

    @Test
    void testContainerDB연결확인() throws Exception {
        System.out.println(">>> Testcontainers MySQL URL: " + mysql.getJdbcUrl());

        System.out.println(">>> Active DB URL = " + dataSource.getConnection().getMetaData().getURL());
        // jdbc:mysql://localhost:61170/...  >>>> Testcontainers,
        // jdbc:mysql://localhost:3306/...  >>> 로컬 DB
    }


    @Test
    void 동시에_여러신청_딱하나만_DB저장_이벤트발행됨() throws Exception {
        // given
        ApplyCardRequest req = new ApplyCardRequest(userId, cardTypeId);

        int threadCount = 5;

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        // CountDownLatch: 검증 단계가 스레드가 끝나기 전에 실행돼서 DB row가 아직 다 안 쌓인 상태에서 체크하는걸 방지
        CountDownLatch latch = new CountDownLatch(threadCount);

        // when : 동시에 5개 요청
        for (int i =0; i < threadCount; i++) {
            executor.submit(() -> {
                try{
                    service.apply(req);
                }catch(Exception e){
                    log.error("스레드에서 예외 발생", e);
                }finally{
                    // 스레드들이 어떤 작업을 끝낼 때마다 countDown() 호출 → 카운터 값이 줄어듦
                   // == 모든 스레드가 끝날 때까지 기다리는 용도
                    latch.countDown();
                }
            });
        }
        latch.await();    // 카운터 0 될때까지 메인 스레드 대기
        executor.shutdown();


        // flush/clear 해서 영속성 컨텍스트 싱크 맞추기
        repository.flush();

        // 커밋 반영 대기 (트랜잭션 지연 대비)
        Thread.sleep(500);

        // then : DB에 row 1개만 존재해야 함
        long count = repository.count();
        System.out.println("count=" + count);
        assertEquals(1,count);

        // kafka 이벤트 발행 확인
        SendResult<String,Object> result = kafkaTemplate.send("card.application.requested","check-event").get();
        assertNotNull(result.getRecordMetadata());

    }

}
