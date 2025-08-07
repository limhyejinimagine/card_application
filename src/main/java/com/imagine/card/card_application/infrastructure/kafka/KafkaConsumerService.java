package com.imagine.card.card_application.infrastructure.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

// Kafka 테스트 -- consumer
@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    @KafkaListener(topics = "test-topic" , groupId = "test-group")
    public void listen(ConsumerRecord<String,String> record) {
        System.out.println("kafka 메시지 수신 = " + record.value());
    }

}
