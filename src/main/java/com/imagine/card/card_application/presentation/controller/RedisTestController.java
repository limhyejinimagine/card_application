package com.imagine.card.card_application.presentation.controller;

import com.imagine.card.card_application.infrastructure.redis.RedisService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

// redis 테스트용 컨트롤러
@RestController
@RequestMapping("/redis")
public class RedisTestController {
    private final RedisService redisService;

    public RedisTestController(RedisService redisService) {
        this.redisService = redisService;
    }

    @PostMapping("/set")
    public String set(@RequestBody Map<String, String> body) {
        String key = body.get("key");
        String value = body.get("value");

        redisService.save(key, value);
        return "saved";
    }

    @GetMapping("/get")
    public String get(@RequestBody Map<String, String> body) {
        String key = body.get("key");
        return redisService.get(key);
    }
}
