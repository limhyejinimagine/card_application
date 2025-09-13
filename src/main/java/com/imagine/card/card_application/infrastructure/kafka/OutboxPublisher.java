package com.imagine.card.card_application.infrastructure.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.imagine.card.card_application.application.service.CardApplicationEventPublisher;
import com.imagine.card.card_application.domain.model.CardApplication;
import com.imagine.card.card_application.domain.model.OutboxStatus;
import com.imagine.card.card_application.domain.repository.OutboxRepository;
import com.imagine.card.card_application.domain.model.OutboxEvent;
import com.imagine.card.card_application.event.CardApplicationStatusChangedEvent;
import com.imagine.card.card_application.infrastructure.notification.EmailSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/* Outbox 이벤트 클래스 */
// TODO : emailsend 는 kafkaconsumer 클래스에서 호출

@Service
@Slf4j
@RequiredArgsConstructor
public class OutboxPublisher {

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * 5 초마다 실행
     * New 상태인 값을 찾아서 kafka 이벤트 발행 & OutBox 상태(이벤트발행상태) 변경
     * 후속처리(알림,메시지발송 등) 는 컨슈머 클래스에서 함
     */
    @Scheduled(fixedRate = 5000)
    @Transactional
    public void outBoxPublish() {

        List<OutboxEvent> newEventList = outboxRepository.findByStatus(OutboxStatus.NEW);

        if (newEventList.isEmpty()) {
            return;
        }

        for (OutboxEvent e : newEventList) {
            String topic = e.getType();
            kafkaTemplate.send(topic, e.getPayload())
                .whenComplete((result, ex) -> {
                   if (ex == null) {
                       e.setSent();
                       outboxRepository.save(e);
                       log.info("kafka 이벤트 발행 성공 : {} " , e.getId());
                   } else {
                       // NEW 상태 그대로 둠
                       log.error("kafka 이벤트 발행 실패 : {} " , e.getId() , ex);
                   }
                });
        }
    }

}
