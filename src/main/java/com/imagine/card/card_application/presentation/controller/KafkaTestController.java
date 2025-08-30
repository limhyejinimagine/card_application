package com.imagine.card.card_application.presentation.controller;

//Kafka 테스트 컨트롤러

import com.imagine.card.card_application.infrastructure.kafka.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

//@RestController
@RequiredArgsConstructor
@RequestMapping("/kafka")
public class KafkaTestController {

    private final KafkaProducerService kafkaProducerService;

    @PostMapping("/send")
    public String sendMessage(@RequestParam String message){
        kafkaProducerService.sendMessage("test-topic", message);
        return "sent message";
    }
}
