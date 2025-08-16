package com.imagine.card.card_application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.imagine.card.card_application.application.dto.ApplyCardRequest;
import com.imagine.card.card_application.domain.model.CardType;
import com.imagine.card.card_application.domain.model.User;
import com.imagine.card.card_application.domain.repository.CardTypeRepository;
import com.imagine.card.card_application.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@EnableAutoConfiguration(exclude = {
		RedisAutoConfiguration.class,
		KafkaAutoConfiguration.class
})
class CardApplicationTests {

	@Autowired
	MockMvc mvc;
	@Autowired
	ObjectMapper om;
	@Autowired
	UserRepository userRepository;
	@Autowired
	CardTypeRepository cardTypeRepository;

	Long userId;
	Long cardTypeId;

	@Sql(statements = {
			"DELETE FROM application_status_history",
			"DELETE FROM card_application",
			"DELETE FROM card_type",
			"DELETE FROM user",
			"INSERT INTO user(user_id, name, phone, birth_date, ci, created_at) VALUES (1,'tester','010-1234-5678','1990-01-01','CI-001', CURRENT_TIMESTAMP)",
			"INSERT INTO card_type(card_type_id, name, description, is_active, created_at) VALUES (1,'BASIC','basic', TRUE, CURRENT_TIMESTAMP)"
	})

	@BeforeEach
	void setUp() {
		var user = userRepository.save(User.builder()
				.name("홍길동").phone("010-1111-2222")
				.birthDate(LocalDate.parse("CI-123"))
				.createdAt(LocalDateTime.now())
				.build());
		userId = user.getId();

		var type = cardTypeRepository.save(CardType.builder()
				.name("Basic")
				.description("기본카드")
				.isActive(true)
				.createdAt(LocalDateTime.now())
				.build());
		cardTypeId = type.getId();
	}

	@Test
	void 신청_해피패스_then201() throws Exception {
		var req = new ApplyCardRequest(userId, cardTypeId);
		mvc.perform(post("/applications")
						.contentType(MediaType.APPLICATION_JSON)
						.content(om.writeValueAsString(req)))
				.andExpect(status().isCreated())
				.andExpect((ResultMatcher) jsonPath("$.applicationId").exists());
	}

	@Test
	void 중복신청_then409() throws Exception {
		var req = new ApplyCardRequest(userId, cardTypeId);
		// 1차 성공
		mvc.perform(post("/applications")
						.contentType(MediaType.APPLICATION_JSON)
						.content(om.writeValueAsString(req)))
				.andExpect(status().isCreated());

		// 2차 중복 → 409
		mvc.perform(post("/applications")
						.contentType(MediaType.APPLICATION_JSON)
						.content(om.writeValueAsString(req)))
				.andExpect(status().isConflict())
				.andExpect((ResultMatcher) jsonPath("$.error").value("CONFLICT"));
	}

}
