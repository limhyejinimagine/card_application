package com.imagine.card.card_application.config.aop;

import com.imagine.card.card_application.event.ValidationFailedEvent;
import com.imagine.card.card_application.infrastructure.redisLock.RedisLockManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;

/* AOP Aspect*/
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class RedisLockAspect {
    private final RedisLockManager redisLockManager;
    @Value("${redis.lock.enabled:true}")
    private boolean lockEnabled;

    @Around("@annotation(withRedisLock)")
    public Object around(ProceedingJoinPoint joinPoint, WithRedisLock withRedisLock) throws Throwable{

        if (!lockEnabled) {
            log.info("Redis Lock 비활성화 상태, 그냥 메서드 실행");
            return joinPoint.proceed();
        }

        // 1. 어노테이션에서 받은 SpEL 을 실제 key 문자열로 변환
        String key = parseKey(joinPoint, withRedisLock.key());

        // 2. Redis 락 시도
        String lockVal = redisLockManager.tryLock(key,withRedisLock.timeout());

        // 3. 락 못잡은 경우 예외 발생 : 중복처리 방지
        if (lockVal == null) {
            log.warn("Redis Lock 실패: key={}", key);
            throw new IllegalStateException("이미 처리중입니다.");
        }

        // 4. 실제 비즈니스 메서드 실행
        try {
            return joinPoint.proceed();
        } finally {
            // 5. 락 해제
            redisLockManager.unlock(key, lockVal);
        }
    }
    
    /**
     * SpEL 을 해석해서 Redis key 문자열 생성
     * */
    private String parseKey(ProceedingJoinPoint joinpoint, String spel) {

        // SpEL 해석기 준비
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();

        // 현재 실행 중인 메서드 정보를 가져온다
        MethodSignature signature = (MethodSignature) joinpoint.getSignature();
        Method method = signature.getMethod();

        // 실제 파라미터 값들
        Object[] args = joinpoint.getArgs();

        // 파라미터 이름들
        String[] paramNames = signature.getParameterNames();

        log.info("실제 파라미터 값들 args = " + Arrays.toString(args));
        log.info("파라미터 이름들 paramNames = " + Arrays.toString(paramNames));

        // 파라미터 이름과 값 매핑
        for (int i=0; i<paramNames.length; i++) {
            context.setVariable(paramNames[i], args[i]);
        }
        log.info("파라미터 이름들 paramNames = " + Arrays.toString(paramNames));

        String result = parser.parseExpression(spel).getValue(context, String.class);

        log.info("SpEL: {}, Result: {}", spel, result);


        // SpEL 해석 -> 최종 문자열 리턴
        return result;
    }

}
