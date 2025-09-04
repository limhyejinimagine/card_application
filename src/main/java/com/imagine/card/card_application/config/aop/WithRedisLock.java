package com.imagine.card.card_application.config.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/* 락 커스텀 애노테이션 정의 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface WithRedisLock {
    String key();   // Redis 락 키를 지정하는 값. 락 키 SpEL 로 정의
    long timeout() default 5;   // 락을 잡는 시간 제한(초 단위), 기본값은 5초
}
