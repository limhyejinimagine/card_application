package com.imagine.card.card_application.infrastructure.redisLock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.UUID;

/* 락 관리 클래스 */

@Component
@Slf4j
@RequiredArgsConstructor
public class RedisLockManager {
    private final RedisTemplate redisTemplate;

    /**
     * 락 획득 시도
     * */
    public String tryLock(String key,long expireSeconds) {
        log.info("tryLock called, key={}, thread={}", key, Thread.currentThread().getName());

        String lockValue = UUID.randomUUID().toString();    // 고유 식별 값(누가 락 잡았는지 : 원자성)
        Boolean success = redisTemplate.opsForValue().setIfAbsent(key, lockValue);

        // 성공- 저장한 lockvalue(UUID) , 실패- null 반환
        return Boolean.TRUE.equals(success) ? lockValue : null;
    }

    /**
     * 락 해제
     * */
    public boolean unlock(String key, String lockValue) {
        // Lua 스크립트 (원자적실행)
        String luaScript =
                "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                        // 해당 키의 값이 내가 저장한 lockValue와 같으면
                        "   return redis.call('del', KEYS[1]) " +
                        //  키 삭제 (락 해제)
                        "else return 0 end";
                        //  값이 다르면 아무 것도 안 함

        // Redis 객체 생성
        RedisScript<Long> redisScript = RedisScript.of(luaScript, Long.class);

        // 실행 (Collections.singletonList -> KEYS[1], ARGV[1] 전달)
        Long result = (Long) redisTemplate.execute(redisScript,
                Collections.singletonList(key), // KEYS
                lockValue); // ARGV

        // 삭제 성공하면 1, 실패/없으면 0
        return result != null && result > 0;
    }

}
