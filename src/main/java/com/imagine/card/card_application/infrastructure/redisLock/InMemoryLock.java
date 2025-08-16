package com.imagine.card.card_application.infrastructure.redisLock;

import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

// 일단 Redis 락 “껍데기”만
@Component
public class InMemoryLock {
    private final Set<String> locks = ConcurrentHashMap.newKeySet();
    public boolean tryLock(String key) { return  locks.add(key); }
    public void release(String key) { locks.remove(key); }
}
