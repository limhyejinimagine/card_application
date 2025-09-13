package com.imagine.card.card_application.application.service.exception;

import jakarta.persistence.OptimisticLockException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/* 동시에 두 관리자가 같은 신청건 변경 시 @Version 충돌 예외 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(OptimisticLockException.class)
    public ResponseEntity<String> handleOptimisticLock(OptimisticLockException e) {
        return ResponseEntity.badRequest().body("이미 처리 된 신청 건입니다.");
    }
}
