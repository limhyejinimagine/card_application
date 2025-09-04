package com.imagine.card.card_application.presentation.controller;

import com.imagine.card.card_application.event.ValidationFailedEvent;
import com.imagine.card.card_application.infrastructure.redis.RedisService;
import com.imagine.card.card_application.infrastructure.redisLock.RedisLockManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

// redis 테스트용 컨트롤러
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/redis")
public class RedisTestController {
    private final RedisService redisService;
    private final RedisLockManager lockManager;

    @GetMapping("/test")
    public String testLock(@RequestParam Long userId, @RequestParam Long cardTypeId) {
        String key = "lock:user" + userId + ":cardType:" + cardTypeId;
        String lockVal = lockManager.tryLock(key, 5);

        if (lockVal == null) {
            log.warn("Redis Lock 실패: userId={}, cardTypeId={}", userId, cardTypeId);
            return "Lock 획득 실패(중복요청)";
        }
        
        try{
            log.info("Redis Lock 성공: userId={}, cardTypeId={}", userId, cardTypeId);
            // 테스트용 지연 -> 동시성 충돌 유도
            try{Thread.sleep(3000);} catch (InterruptedException e){}
            return "Lock 획득 성공";
        } finally {
            lockManager.unlock(key, lockVal);
            log.info("Redis Lock 해제 : key={}" , key);
        }

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
