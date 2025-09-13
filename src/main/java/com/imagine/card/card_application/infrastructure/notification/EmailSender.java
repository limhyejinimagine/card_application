package com.imagine.card.card_application.infrastructure.notification;

/* 외부 이메일 발송 인터페이스 */
public interface EmailSender {
    void send (String to, String subject, String body);
}
